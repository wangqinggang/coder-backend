package com.ideaworks.club.domain.order;

import com.yungouos.pay.wxpay.WxPay;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@ApiOperation("微信支付")
@CrossOrigin
@RequestMapping(value = "/api/WechatPay")
@Tag(name = "微信支付")
public class WechatPayController {

    String mchId = "";
    String key = "";
//    String notify_url ="http://www.ideaworks.club:8888/api/WechatPay/getResult";

    @GetMapping("/{price}")
    public String getImage(@PathVariable Integer price){
        String out_trade_no = UUID.randomUUID()+"";
        System.out.println(price);
        String total_fee = Math.abs(price)+"";
        String result = WxPay.nativePay(out_trade_no, total_fee, mchId, "赞助网站维护", null, null, null, null,null,null,null,null,key);
        return result;
    }

}
