package dd.appchecker.aws.data;

/**
 * Created by Dejan on 22.10.2014.
 */
public class DownloadInfo {

    String bucketName;
    String key;

    public DownloadInfo(String bucket, String key) {
        this.bucketName = bucket;
        this.key = key;
    }

    public String getBucketKeyName() {
        return bucketName + "_" + key;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
