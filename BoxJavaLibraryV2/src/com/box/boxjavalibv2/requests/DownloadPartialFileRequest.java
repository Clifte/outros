package com.box.boxjavalibv2.requests;

import org.apache.http.HttpStatus;

import com.box.boxjavalibv2.IBoxConfig;
import com.box.boxjavalibv2.jsonparsing.IBoxJSONParser;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.BoxDefaultRequestObject;

public class DownloadPartialFileRequest extends DownloadFileRequest {

    public DownloadPartialFileRequest(IBoxConfig config, IBoxJSONParser parser, String fileId, BoxDefaultRequestObject requestObject) throws BoxRestException {
        super(config, parser, fileId, requestObject);
        this.setExpectedResponseCode(HttpStatus.SC_PARTIAL_CONTENT);
    }

}
