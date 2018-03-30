package com.hiekn.boot.autoconfigure.qiniu;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("qiniu")
public class QiNiuProperties {

    private String ak;
    private String sk;
    private String bucket;
    private String bucketPath;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getBucketPath() {
        return bucketPath;
    }

    public void setBucketPath(String bucketPath) {
        this.bucketPath = bucketPath;
    }

}