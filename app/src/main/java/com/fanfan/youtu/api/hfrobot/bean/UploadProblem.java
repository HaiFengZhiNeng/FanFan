package com.fanfan.youtu.api.hfrobot.bean;

/**
 * Created by Administrator on 2018/3/29/029.
 */

public class UploadProblem extends RobotMsg {


    /**
     * code : 2
     * msg : 已经添加过
     * UploadProblem : {"id":"41","identifier":"hotel001","problem":"对我","answer":null}
     */

    private UploadProblemBean UploadProblem;

    public UploadProblemBean getUploadProblem() {
        return UploadProblem;
    }

    public void setUploadProblem(UploadProblemBean UploadProblem) {
        this.UploadProblem = UploadProblem;
    }

    public static class UploadProblemBean {
        /**
         * id : 41
         * identifier : hotel001
         * problem : 对我
         * answer : null
         */

        private String id;
        private String identifier;
        private String problem;
        private String answer;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getProblem() {
            return problem;
        }

        public void setProblem(String problem) {
            this.problem = problem;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
