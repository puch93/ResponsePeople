package kr.co.core.responsepeople.server;

import android.content.Context;
import android.util.Log;

import kr.co.core.responsepeople.server.netUtil.BaseReq;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.util.StringUtil;


public abstract class ReqBasic extends BaseReq {
    public ReqBasic(Context context, String url) {
        super(context, url);
    }

    @Override
    public HttpResult onParse(String jsonString) {

        HttpResult res = new HttpResult();
        if (StringUtil.isNull(jsonString)){
            res.setResult(null);
            if(!StringUtil.isNull(TAG))
                Log.e(StringUtil.TAG, TAG + " Get Info: null");

        }else{
            res.setResult(jsonString);
            if(!StringUtil.isNull(TAG))
                Log.e(StringUtil.TAG, TAG + " Get Info: " + jsonString);

        }
        return res;
    }
}
