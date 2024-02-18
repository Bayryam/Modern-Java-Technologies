package bg.sofia.uni.fmi.mjt.crypto.dto;

import bg.sofia.uni.fmi.mjt.crypto.cache.Cacheable;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public record Equity(@SerializedName("asset_id")String equityId,
                     @SerializedName("name")String equityName,
                     @SerializedName("type_is_crypto")int isCryptoCurrency,
                     @SerializedName("price_usd")BigDecimal priceInUSD) implements Cacheable {
}
