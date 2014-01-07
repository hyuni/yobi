/**
 * Yobi, Project Hosting SW
 *
 * Copyright 2013 NAVER Corp.
 * http://yobi.io
 *
 * @Author Wansoon Park
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package actors;

import models.PullRequest;
import models.PullRequestEventMessage;

/**
 * message로 전달된 pullRequest에 대해 병합을 시도하고 결과를 저장한다.
 * @author Wansoon Park
 *
 */
public class PullRequestMergingActor extends PullRequestActor {
    @Override
    public void onReceive(Object object) {
        if (!(object instanceof PullRequestEventMessage)) {
            return;
        }

        PullRequestEventMessage message = (PullRequestEventMessage) object;
        PullRequest pullRequest = message.getPullRequest();

        pullRequest.startMerge();
        pullRequest.update();

        processPullRequestMerging(message, pullRequest);
    }
}
