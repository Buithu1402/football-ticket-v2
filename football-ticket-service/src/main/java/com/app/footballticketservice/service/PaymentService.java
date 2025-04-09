package com.app.footballticketservice.service;

import com.app.footballticketservice.config.SystemConfigService;
import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.request.payload.StripeRequest;
import com.app.footballticketservice.utils.Constants;
import com.app.footballticketservice.utils.VnpayUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@Log
@Service
public class PaymentService {
    private final NamedParameterJdbcTemplate writeDb;
    private final SystemConfigService systemConfigService;

    public PaymentService(@WriteDB NamedParameterJdbcTemplate writeDb, SystemConfigService systemConfigService) {
        this.writeDb = writeDb;
        this.systemConfigService = systemConfigService;
    }

    public String vnPay(String txnRef, long amount, String info) {
        var vnpReturnUrl = systemConfigService.getConfigValue(VnpayUtils.VN_PAY_RETURN_URL);
        var vnpParams = VnpayUtils.buildParamVnPay(amount, info, txnRef, vnpReturnUrl);

        var cld = Calendar.getInstance(TimeZone.getTimeZone(VnpayUtils.TIME_ZONE_DEFAULT));
        var formatter = new SimpleDateFormat(VnpayUtils.DATE_FORMAT);
        var vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put(VnpayUtils.VNP_CREATE_DATE_KEY, vnpCreateDate);
        cld.add(Calendar.MINUTE, VnpayUtils.DEFAULT_TIME_END);

        var vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put(VnpayUtils.VNP_EXPIRE_DATE_KEY, vnpExpireDate);
        var fieldNames = vnpParams.keySet().stream().sorted().toList();
        var hashData = new StringBuilder();
        var query = new StringBuilder();
        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = vnpParams.get(fieldName);
            if (StringUtils.isNotBlank(fieldValue)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        var secureHash = VnpayUtils.hmacSHA512(VnpayUtils.VNP_SECRET_KEY_VALUE, hashData.toString());
        var paymentUrl = MessageFormat.format(
                "{0}?{1}&{2}={3}",
                VnpayUtils.VNP_PAY_URL,
                query,
                VnpayUtils.VNP_SECURE_HASH_TYPE_KEY,
                secureHash
        );
        log.info(MessageFormat.format("VNPAY Payment URL: {0}", paymentUrl));
        return paymentUrl;
    }

    public String stripe(String email, long amount, String name, String txnRef) throws StripeException {
        var request = StripeRequest.builder()
                                   .amount(amount)
                                   .name(name)
                                   .stripeEmail(email)
                                   .currency(StripeRequest.Currency.VND)
                                   .build();
        Stripe.apiKey = systemConfigService.getConfigValue(Constants.STRIPE_API_KEY);
        var returnUrl = systemConfigService.getConfigValue(Constants.STRIPE_RETURN_URL);
        var productData = SessionCreateParams
                .LineItem.PriceData.ProductData
                .builder()
                .setName(request.getName())
                .setDescription(request.getDescription())
                .build();
        var priceData = SessionCreateParams
                .LineItem.PriceData
                .builder()
                .setCurrency(request.getCurrency().name())
                .setUnitAmount(request.getAmount())
                .setProductData(productData)
                .build();
        var lineItem = SessionCreateParams
                .LineItem
                .builder()
                .setQuantity(request.getQuantity())
                .setPriceData(priceData)
                .setQuantity(1L)
                .build();
        var sessionParam = SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(request.getStripeEmail())
                .setSuccessUrl(MessageFormat.format("{0}&s=success&txnRef={1}", returnUrl, txnRef))
                .setCancelUrl(MessageFormat.format("{0}&s=cancel&txnRef={1}", returnUrl, txnRef))
                .addLineItem(lineItem)
                .build();
        var session = Session.create(sessionParam);
        return session.getUrl();
    }
}
