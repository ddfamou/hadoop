/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.fs.aliyun.oss;

import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.InvalidCredentialsException;
import com.aliyun.oss.common.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.auth.AlibabaCloudCredentials;
import com.aliyuncs.auth.InstanceProfileCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


/**
 * Support assumed role credentials for authenticating with Aliyun.
 * roleArn is configured in core-site.xml
 */
public class AssumedRoleCredentialProvider implements CredentialsProvider {
  private static final Logger LOG =
      LoggerFactory.getLogger(AssumedRoleCredentialProvider.class);
  public static final String NAME
      = "org.apache.hadoop.fs.aliyun.oss.AssumedRoleCredentialProvider";
  private Credentials credentials;
  private String roleArn;
  private String roleName;
  private String stsEndpoint;
  private CredentialsProvider credentialsProvider;

  public AssumedRoleCredentialProvider(URI uri, Configuration conf) {
    roleArn = conf.getTrimmed(Constants.ROLE_ARN, "");
    if (StringUtils.isEmpty(roleArn)) {
      throw new InvalidCredentialsException(Constants.ROLE_ARN + " is empty");
    }

    stsEndpoint = conf.getTrimmed(Constants.ASSUMED_ROLE_STS_ENDPOINT, "");
    if (StringUtils.isEmpty(stsEndpoint)) {
      throw new InvalidCredentialsException(
          "fs.oss.assumed.role.sts.endpoint is empty");
    }

    try {
      DefaultProfile.addEndpoint("", "", "Sts", stsEndpoint);
    } catch (ClientException e) {
      throw new InvalidCredentialsException(e);
    }
    roleName = conf.getTrimmed(Constants.ROLE_NAME, "");
    if (StringUtils.isEmpty(roleName)) {
      throw new InvalidCredentialsException(Constants.ROLE_NAME + " is empty");
    }
    credentialsProvider = new com.aliyun.oss.common.auth.InstanceProfileCredentialsProvider(roleName);
  }

  @Override
  public void setCredentials(Credentials creds) {
    throw new InvalidCredentialsException(
        "Should not set credentials from external call");
  }

  @Override
  public Credentials getCredentials() {
    credentials = credentialsProvider.getCredentials();
    if (credentials == null) {
      throw new InvalidCredentialsException("Invalid credentials");
    }
    return credentials;
  }
}
