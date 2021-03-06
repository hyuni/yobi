/**
 * Yobi, Project Hosting SW
 *
 * Copyright 2013 NAVER Corp.
 * http://yobi.io
 *
 * @Author Yi EungJun
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
package models;

import models.enumeration.ResourceType;
import models.resource.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

@Entity
public class CommitComment extends CodeComment {
    private static final long serialVersionUID = 1L;
    public static final Finder<Long, CommitComment> find = new Finder<>(Long.class, CommitComment.class);

    @Transient
    public List<CommitComment> replies = new ArrayList<>();

    public String commitId;

    public CommitComment() {
        super();
    }

    @Override
    public Resource asResource() {
        return new Resource() {
            @Override
            public String getId() {
                return id.toString();
            }

            @Override
            public Project getProject() {
                return project;
            }

            @Override
            public ResourceType getType() {
                return ResourceType.COMMIT_COMMENT;
            }

            @Override
            public Long getAuthorId() {
                return authorId;
            }

            public void delete() {
                CommitComment.this.delete();
            }
        };
    }

    public static int count(Project project, String commitId, String path){
        if(path != null){
            return CommitComment.find.where()
                    .eq("project.id", project.id)
                    .eq("commitId", commitId)
                    .eq("path", path)
                    .findRowCount();
        } else {
            return CommitComment.find.where()
                    .eq("project.id", project.id)
                    .eq("commitId", commitId)
                    .findRowCount();
        }
    }

    public static int countByCommits(Project project, List<PullRequestCommit> commits) {
        int count = 0;
        for(PullRequestCommit commit: commits) {
            count += CommitComment.find.where().eq("project.id", project.id)
                                .eq("commitId", commit.getCommitId())
                                .findRowCount();
        }

        return count;
    }

    public static List<CommitComment> findByCommits(Project project, List<PullRequestCommit> commits) {
        List<CommitComment> list = new ArrayList<>();
        for(PullRequestCommit commit: commits) {
            list.addAll(CommitComment.find.where().eq("project.id", project.id).eq("commitId", commit.getCommitId()).setOrderBy("createdDate asc").findList());
        }
        return list;
    }

    /**
     * CommitComment의 groupKey를 반환한다.
     * commitId, path, line정보를 조한한 키가 일치할 경우 동일한 내용에 대한
     * 코멘트로 간주한다.
     *
     * @return
     */
    public String groupKey() {
        return new StringBuilder().append(this.commitId)
                .append(this.path).append(this.line).toString();
    }

    public boolean threadEquals(CommitComment other) {
        return commitId.equals(other.commitId) &&
                path.equals(other.path) &&
                line.equals(other.line) &&
                side.equals(other.side);
    }

    public String getCommitId() {
        return commitId;
    }

    /**
     * 코멘트가 작성된 위치 정보가 있는지 여부를 반환한다.
     * @return path와 line정보가 있으면 true 아니면 false
     */
    public boolean hasLocation() {
        return StringUtils.isNotBlank(this.path) && this.line != null;
    }
}
