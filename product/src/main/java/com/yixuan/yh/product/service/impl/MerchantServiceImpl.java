package com.yixuan.yh.product.service.impl;

import com.yixuan.mt.client.MTClient;
import com.yixuan.yh.commom.utils.SnowflakeUtils;
import com.yixuan.yh.product.mapper.*;
import com.yixuan.yh.product.mapper.multi.SkuMapper;
import com.yixuan.yh.product.mapstruct.MerchantMapStruct;
import com.yixuan.yh.product.model.entity.Product;
import com.yixuan.yh.product.model.entity.ProductSku;
import com.yixuan.yh.product.model.entity.SkuSpec;
import com.yixuan.yh.product.model.multi.SkuSpecInfo;
import com.yixuan.yh.product.request.PostSkuSpecRequest;
import com.yixuan.yh.product.request.PutProductBasicInfoRequest;
import com.yixuan.yh.product.request.PutSkuRequest;
import com.yixuan.yh.product.response.ProductEditResponse;
import com.yixuan.yh.product.response.ProductManageItemResponse;
import com.yixuan.yh.product.service.MerchantService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpecKeyMapper specKeyMapper;
    @Autowired
    private SpecValueMapper specValueMapper;
    @Autowired
    private SkuSpecMapper skuSpecMapper;
    @Autowired
    private MTClient mtClient;
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Override
    public List<ProductManageItemResponse> getMerchantProduct(Long user) {
        return productMapper.selectMerchantProducts(user);
    }

    @Override
    public ProductEditResponse getMerchantProductEditData(Long productId) {
        ProductEditResponse productEditResponse = new ProductEditResponse();
        Product product = productMapper.selectEditBasicData(productId);
        // 商品基本信息
        productEditResponse.setTitle(product.getTitle());
        productEditResponse.setPrice(product.getPrice());
        productEditResponse.setDescription(product.getDescription());
        productEditResponse.setCoverUrl(product.getCoverUrl());
        // 商品轮播图

        // 商品SKU
        List<ProductSku> skuList = productSkuMapper.selectByProductId(productId);
        if (!skuList.isEmpty()) {
            List<Long> skuIdList = skuList.stream().map(ProductSku::getSkuId).toList();

            List<SkuSpecInfo> specList = skuMapper.selectSpecsBySkuIds(skuIdList);

            Map<Long, List<SkuSpecInfo>> specMap = specList.stream()
                    .collect(Collectors.groupingBy(SkuSpecInfo::getSkuId));

            List<ProductEditResponse.SkuDetailDTO> skuDetailDTOList = skuList.stream()
                    .map(sku -> new ProductEditResponse.SkuDetailDTO(
                            sku.getSkuId(),
                            sku.getPrice(),
                            sku.getStock(),
                            specMap.getOrDefault(sku.getSkuId(), emptyList()).stream()
                                    .map(skuSpecInfo -> new ProductEditResponse.SkuDetailDTO.SpecPair(skuSpecInfo.getSpecKey(), skuSpecInfo.getSpecValue(), skuSpecInfo.getSpecKeyId(), skuSpecInfo.getSpecValueId()))
                                    .toList()
                    )).toList();
            productEditResponse.setProductSkuList(skuDetailDTOList);
        }

        return productEditResponse;
    }

    @Override
    @Transactional
    public void postSkuSpec(PostSkuSpecRequest postSkuSpecRequest) {
        if (postSkuSpecRequest.getType().equals("new")) {
            /* 代表新增规格此时不会产生新的 SKU（除非没有一个 SKU），但是会让每个 SKU 新增一对 Spec。*/
            PostSkuSpecRequest.Spec spec = postSkuSpecRequest.getSpec();
            // 插入规格键和值
            Long keyId = snowflakeUtils.nextId();
            Long valueId = snowflakeUtils.nextId();
            specKeyMapper.insert(keyId, spec.getKey());
            specValueMapper.insert(valueId, spec.getValue());
            // 获取 product 的所有 SKU
            List<Long> skuIdList = productSkuMapper.selectSkuIdByProductId(postSkuSpecRequest.getProductId());
            if (skuIdList.isEmpty()) { // 代表原先没有任何 SKU
                Long skuId = snowflakeUtils.nextId();
                skuIdList.add(skuId);
                productSkuMapper.insertBatch(postSkuSpecRequest.getProductId(), skuIdList);
            }
            // 当前 product 的每个 SKU 新增规格键值对
            skuSpecMapper.insertBatch1(skuIdList, keyId, valueId);
        } else {
            /* 代表不是新增规格，此时会新增 SKU */
            PostSkuSpecRequest.Spec spec = postSkuSpecRequest.getSpec();
            // 插入新规格值
            Long valueId = snowflakeUtils.nextId();
            specValueMapper.insert(valueId, spec.getValue());
            // 生成新的 SKU 的 id
            List<Long> skuIdList = new ArrayList<>(postSkuSpecRequest.getSkus().size());
            for (int i = 0; i < postSkuSpecRequest.getSkus().size(); i++) {
                skuIdList.add(snowflakeUtils.nextId());
            }
            // 遍历所有 SKU 填充缺失的 id
            for (List<PostSkuSpecRequest.SpecId> sku : postSkuSpecRequest.getSkus()) {
                for (PostSkuSpecRequest.SpecId specId : sku) {
                    if (specId.getValueId() == null) {
                        specId.setValueId(valueId);
                    }
                }
            }
            // 插入规格键值对
            skuSpecMapper.insertBatch2(skuIdList, postSkuSpecRequest.getSkus());
            // 建立 Product 与 SKU 的映射关系
            productSkuMapper.insertBatch(postSkuSpecRequest.getProductId(), skuIdList);
        }
    }

    @Override
    @Transactional
    public void deleteMerchantProduct(Long productId) {
        // 删除商品基本信息
        productMapper.deleteByProductId(productId);
        // 获取商品的所有 SKU Id
        List<Long> skuIdList = productSkuMapper.selectSkuIdByProductId(productId);
        // 删除商品的 SKU
        productSkuMapper.deleteBatch(skuIdList);
        // 获取商品的所有 Key、Value Id
        List<SkuSpec> skuSpecList = skuSpecMapper.selectKeyValueBatch(skuIdList);
        List<Long> keyIdList = new ArrayList<>(skuSpecList.size());
        List<Long> valueIdList = new ArrayList<>(skuSpecList.size());
        for (SkuSpec skuSpec : skuSpecList) {
            keyIdList.add(skuSpec.getKeyId());
            valueIdList.add(skuSpec.getValueId());
        }
        // 删除商品的 SKU 关联的 Spec
        skuSpecMapper.deleteBatch(skuIdList);
        // 删除 Spec Key
        specKeyMapper.deleteBatch(keyIdList);
        // 删除 Spec Value
        specValueMapper.deleteBatch(valueIdList);
    }

    @Override
    public void putMerchantProductBasicInfo(Long productId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        Product product = MerchantMapStruct.INSTANCE.putProductBasicInfoRequestToProduct(putProductBasicInfoRequest);
        product.setProductId(productId);
        if (putProductBasicInfoRequest.getCover() != null) {
            product.setCoverUrl(mtClient.upload(putProductBasicInfoRequest.getCover()));
        }
        productMapper.updateBasicInfo(product);
    }

    @Override
    public void postMerchantProduct(Long merchantId, PutProductBasicInfoRequest putProductBasicInfoRequest) throws IOException {
        Product product = MerchantMapStruct.INSTANCE.putProductBasicInfoRequestToProduct(putProductBasicInfoRequest);
        product.setProductId(snowflakeUtils.nextId());
        product.setMerchantId(merchantId);
        if (putProductBasicInfoRequest.getCover() != null) {
            product.setCoverUrl(mtClient.upload(putProductBasicInfoRequest.getCover()));
        }
        productMapper.insertBasicInfo(product);
    }

    @Override
    public void putSku(Long userId, PutSkuRequest putSkuRequest) throws BadRequestException {
        // 鉴权
        List<Long> skuIdList = putSkuRequest.getChangeSkuList().stream().map(PutSkuRequest.PutSku::getSkuId).toList();
        List<Long> merchantIdList;
        if ((merchantIdList = skuMapper.selectMerchantIdBySkuIds(skuIdList)).size() > 1 || !merchantIdList.get(0).equals(userId)) {
            throw new BadRequestException("你没有权限！");
        }

        List<ProductSku> productSkuList = new ArrayList<>(putSkuRequest.getChangeSkuList().size());
        for (PutSkuRequest.PutSku putSku : putSkuRequest.getChangeSkuList()) {
            productSkuList.add(MerchantMapStruct.INSTANCE.putSkuRequestToProductSku(putSku));
        }

        productSkuMapper.update(productSkuList);
    }
}
