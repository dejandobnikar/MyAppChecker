package dd.appchecker.aws.tasks;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dejan on 22.10.2014.
 */
public class AWSBucketProviderImpl implements AWSBucketProvider {


    private static List<String> listBuckets = Arrays.asList(new String[] {
            "bucket 1",
            "bucket 2"
    });

    @Override
    public List<String> getBucketList() {
        return listBuckets;
    }
}
