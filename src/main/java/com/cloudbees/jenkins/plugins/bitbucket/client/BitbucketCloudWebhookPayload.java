/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cloudbees.jenkins.plugins.bitbucket.client;

import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPullRequestEvent;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPushEvent;
import com.cloudbees.jenkins.plugins.bitbucket.client.events.BitbucketCloudPullRequestEvent;
import com.cloudbees.jenkins.plugins.bitbucket.client.events.BitbucketCloudPushEvent;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONArray;

public class BitbucketCloudWebhookPayload {

    private static final Logger LOGGER = Logger.getLogger(BitbucketCloudWebhookPayload.class.getName());

    @CheckForNull
    public static BitbucketPushEvent pushEventFromPayload(@NonNull String payload) {
        try {
            BitbucketCloudPushEvent push = parse(payload, BitbucketCloudPushEvent.class);
            JSONArray changes = new JSONObject(payload).getJSONObject("push").getJSONArray("changes");                
            JSONObject change = (JSONObject)changes.get(0);            
            String commitMessage = change.getJSONObject("new").getJSONObject("target").getString("message");
            push.setCommitMessage(commitMessage);                       
            return push;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not read hook payload", e);
        }
        return null;
    }

    @CheckForNull
    public static BitbucketPullRequestEvent pullRequestEventFromPayload(@NonNull String payload) {
        try {
            return parse(payload, BitbucketCloudPullRequestEvent.class);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not read hook payload", e);
        }
        return null;
    }

    private static <T> T parse(String response, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, clazz);
    }

}
