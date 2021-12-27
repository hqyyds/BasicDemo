package com.h3w;

import com.h3w.enums.CommonEnum;
import com.h3w.exception.BaseErrorInfoInterface;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 数据返回结果类
 *
 * @author hyyds
 * @date 2021/6/16
 */
public class ResultObject {

    // 状态码
    public final static int STATUS_CODE_SUCCESS = 200;
    public final static int STATUS_CODE_FAILURE = 300;

    /**
     * 响应代码
     */
    private Integer statusCode;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object data;

    public ResultObject() {
    }

    /**
     * 构造函数
     *
     * @param statusCode
     * @param message
     */
    public ResultObject(int statusCode, String message) {
        super();
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResultObject(BaseErrorInfoInterface errorInfo) {
        this.statusCode = errorInfo.getResultCode();
        this.message = errorInfo.getResultMsg();
    }

    public Integer getCode() {
        return statusCode;
    }

    public void setCode(Integer code) {
        this.statusCode = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 成功
     *
     * @return
     */
    public static ResultObject success() {
        return success(null);
    }

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static ResultObject success(Object data) {
        ResultObject rb = new ResultObject();
        rb.setCode(CommonEnum.SUCCESS.getResultCode());
        rb.setMessage(CommonEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultObject error(BaseErrorInfoInterface errorInfo) {
        ResultObject rb = new ResultObject();
        rb.setCode(errorInfo.getResultCode());
        rb.setMessage(errorInfo.getResultMsg());
        rb.setData(null);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultObject error(Integer code, String message) {
        ResultObject rb = new ResultObject();
        rb.setCode(code);
        rb.setMessage(message);
        rb.setData(null);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultObject error(String message) {
        ResultObject rb = new ResultObject();
        rb.setCode(STATUS_CODE_FAILURE);
        rb.setMessage(message);
        rb.setData(null);
        return rb;
    }

    public static ResultObject newOk(String message) {
        return new ResultObject(STATUS_CODE_SUCCESS, message);
    }

    public static ResultObject newError(String message) {
        return new ResultObject(STATUS_CODE_FAILURE, message);
    }

    public static JSONObject newJSONData(JSONObject data) {
        JSONObject object = new JSONObject();
        object.put("statusCode", STATUS_CODE_SUCCESS);
        object.put("data", data);
        return object;
    }

    public static JSONObject newJSONRows(JSONArray rows) {
        JSONObject object = new JSONObject();
        object.put("statusCode", STATUS_CODE_SUCCESS);
        object.put("data", rows);
        return object;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{")
                .append("\"statusCode\":" + statusCode + ",")
                .append("\"message\":\"" + message + "\",")
                .append("\"data\":" + data + "")
                .append("}");
        return buffer.toString();
    }
}
