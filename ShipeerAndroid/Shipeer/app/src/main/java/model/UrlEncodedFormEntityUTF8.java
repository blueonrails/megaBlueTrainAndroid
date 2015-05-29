package model;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by mifercre on 05/05/15.
 */
public class UrlEncodedFormEntityUTF8 extends UrlEncodedFormEntity {

    public UrlEncodedFormEntityUTF8(List<NameValuePair> nameValuePairs) throws UnsupportedEncodingException {
        super(nameValuePairs, "UTF_8");
    }
}
