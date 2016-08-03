package com.che.acommon.web;

/**
 * Created by soap on 16/6/21.
 */
public interface FileDownloadListener {
    void onProgress(int bytesRead, int contentLength);
    void onFinish(boolean success);
}
