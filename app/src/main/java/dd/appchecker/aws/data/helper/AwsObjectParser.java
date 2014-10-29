package dd.appchecker.aws.data.helper;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 21.10.2014.
 */
public interface AwsObjectParser {

    ApkInfo parseObject(S3ObjectSummary objectSummary);


}
