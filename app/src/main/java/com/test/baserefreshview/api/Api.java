package com.test.baserefreshview.api;

        import com.xycode.xylibrary.utils.serverApiHelper.ApiHelper;

        import java.util.List;

/**
 * Created by XY on 2017-06-07.
 *
 */

public class Api extends ApiHelper {

    public static Api api() {
        if (api == null) {
            api = new Api();
        }
        return (Api) api;
    }

    @Override
    protected String getReleaseUrl() {
        return "http://www.taichi-tiger.com:8080";
    }

    @Override
    protected String getDebugUrl() {
        return "https://www.taichi-tiger.com:8080";
    }

    @Override
    protected List<String> setOptionUrlList(List<String> serverList) {
        serverList.add("http://www.taichi-tiger.com:1111");
        serverList.add("http://www.taichi-tiger.com:2222");
        serverList.add("http://www.taichi-tiger.com:3333");
        return serverList;
    }

    /**
     *
     */
    public final String getSomeAddress = getServer("/append/app_poster/selectAllPosters");

    public final String uploadFiles = "http://139.159.219.128:8080" + "/mobile/accident/save";
}
