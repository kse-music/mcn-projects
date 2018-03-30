package com.hiekn.boot.autoconfigure.qiniu;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

public class QiNiu {

    private QiNiuProperties properties;

    public QiNiu(QiNiuProperties properties) {
        this.properties = properties;
    }

    public void upload(final byte[] data, final String fileName) throws QiniuException {
        Configuration cfg = new Configuration(Zone.zone0());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(properties.getAk(), properties.getSk());
        String token = auth.uploadToken(properties.getBucket());
        uploadManager.put(data, fileName, token);
    }
}
