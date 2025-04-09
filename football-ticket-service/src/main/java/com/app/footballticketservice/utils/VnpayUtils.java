package com.app.footballticketservice.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

@UtilityClass
public class VnpayUtils {
    public static final String VNP_REQUEST_ID = "vnp_RequestId";
    public static final String VNP_VERSION_KEY = "vnp_Version";
    public static final String VNP_COMMAND_KEY = "vnp_Command";
    public static final String VNP_TMN_CODE_KEY = "vnp_TmnCode";
    public static final String VNP_AMOUNT_KEY = "vnp_Amount";
    public static final String VNP_CURR_CODE_KEY = "vnp_CurrCode";
    public static final String VNP_BANK_CODE_KEY = "vnp_BankCode";
    public static final String VNP_TXN_REF_KEY = "vnp_TxnRef";
    public static final String VNP_ORDER_INFO_KEY = "vnp_OrderInfo";
    public static final String VNP_RETURN_URL_KEY = "vnp_ReturnUrl";
    public static final String VNP_IP_ADDR_KEY = "vnp_IpAddr";
    public static final String VNP_ORDER_TYPE_KEY = "vnp_OrderType";
    public static final String VNP_LOCALE_KEY = "vnp_Locale";
    public static final String VNP_CREATE_DATE_KEY = "vnp_CreateDate";
    public static final String VNP_TRANSACTION_DATE_KEY = "vnp_TransactionDate";
    public static final String VNP_EXPIRE_DATE_KEY = "vnp_ExpireDate";
    public static final String VNP_SECURE_HASH_TYPE_KEY = "vnp_SecureHash";

    public static final String VNP_VERSION_VALUE = "2.1.0";
    public static final String VNP_COMMAND_VALUE = "pay";
    public static final String VNP_COMMAND_QUERY_VALUE = "querydr";
    public static final String VNP_CURR_CODE_VALUE = "VND";
    public static final String VNP_TMN_CODE_VALUE = "GLE8YXG4";
    public static final String VNP_SECRET_KEY_VALUE = "ZCVPMHAELZKRPGTFLWJDPLQVPHBWEKXG";
    public static final String VNP_ORDER_TYPE_VALUE = "other";
    public static final String VNP_LOCALE_VALUE = "vn";
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_QUERY_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    public static final String ALGORITHM_HMAC = "HmacSHA512";
    public static final String VN_PAY_RETURN_URL = "VN_PAY_RETURN_URL";
    public static final String TIME_ZONE_DEFAULT = "Etc/GMT+7";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final int DEFAULT_TIME_END = 15;

    private static final Random rnd = new Random();

    public String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final var hmac512 = Mac.getInstance(ALGORITHM_HMAC);
            var hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, ALGORITHM_HMAC);
            hmac512.init(secretKey);
            var dataBytes = data.getBytes(StandardCharsets.UTF_8);
            var result = hmac512.doFinal(dataBytes);
            var sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ignored) {
            return "";
        }
    }

    public static String getRandomNumber(int len) {
        var chars = "0123456789";
        var sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public HashMap<String, String> buildParamVnPay(
            long amount,
            String info,
            String vnpTxnRef,
            String vnpReturnUrl
    ) {
        var vnpParams = new HashMap<String, String>();
        vnpParams.put(VnpayUtils.VNP_VERSION_KEY, VnpayUtils.VNP_VERSION_VALUE);
        vnpParams.put(VnpayUtils.VNP_COMMAND_KEY, VnpayUtils.VNP_COMMAND_VALUE);
        vnpParams.put(VnpayUtils.VNP_TMN_CODE_KEY, VnpayUtils.VNP_TMN_CODE_VALUE);
        vnpParams.put(VnpayUtils.VNP_AMOUNT_KEY, String.valueOf(amount));
        vnpParams.put(VnpayUtils.VNP_CURR_CODE_KEY, VnpayUtils.VNP_CURR_CODE_VALUE);
        vnpParams.put(VnpayUtils.VNP_BANK_CODE_KEY, StringUtils.EMPTY);
        vnpParams.put(VnpayUtils.VNP_TXN_REF_KEY, vnpTxnRef);
        vnpParams.put(VnpayUtils.VNP_ORDER_INFO_KEY, info);
        vnpParams.put(VnpayUtils.VNP_RETURN_URL_KEY, vnpReturnUrl);
        vnpParams.put(VnpayUtils.VNP_IP_ADDR_KEY, ServerHelper.getClientIp());
        vnpParams.put(VnpayUtils.VNP_ORDER_TYPE_KEY, VnpayUtils.VNP_ORDER_TYPE_VALUE);
        vnpParams.put(VnpayUtils.VNP_LOCALE_KEY, VnpayUtils.VNP_LOCALE_VALUE);
        return vnpParams;
    }

    private HashMap<String, String> buildParamQuery(String vnpTxnRef, String transDate) {
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        var vnp_CreateDate = formatter.format(cld.getTime());
        var vnpParams = new HashMap<String, String>();
        var requestId = VnpayUtils.getRandomNumber(8);
        var vnIpAddr = ServerHelper.getClientIp();
        vnpParams.put(VnpayUtils.VNP_REQUEST_ID, requestId);
        vnpParams.put(VnpayUtils.VNP_VERSION_KEY, VnpayUtils.VNP_VERSION_VALUE);
        vnpParams.put(VnpayUtils.VNP_COMMAND_KEY, VnpayUtils.VNP_COMMAND_QUERY_VALUE);
        vnpParams.put(VnpayUtils.VNP_TMN_CODE_KEY, VnpayUtils.VNP_TMN_CODE_VALUE);
        vnpParams.put(VnpayUtils.VNP_TXN_REF_KEY, vnpTxnRef);
        vnpParams.put(VnpayUtils.VNP_ORDER_INFO_KEY, vnpTxnRef);
        vnpParams.put(VnpayUtils.VNP_TRANSACTION_DATE_KEY, transDate);
        vnpParams.put(VnpayUtils.VNP_CREATE_DATE_KEY, vnp_CreateDate);
        vnpParams.put(VnpayUtils.VNP_IP_ADDR_KEY, vnIpAddr);
        var hash_Data = String.join(
                "|",
                requestId,
                VnpayUtils.VNP_VERSION_VALUE,
                VnpayUtils.VNP_COMMAND_QUERY_VALUE,
                VnpayUtils.VNP_TMN_CODE_VALUE,
                vnpTxnRef,
                transDate,
                vnp_CreateDate,
                vnIpAddr,
                vnpTxnRef
        );
        var vnp_SecureHash = VnpayUtils.hmacSHA512(VnpayUtils.VNP_SECRET_KEY_VALUE, hash_Data);
        vnpParams.put(VnpayUtils.VNP_SECURE_HASH_TYPE_KEY, vnp_SecureHash);
        return vnpParams;
    }
}
