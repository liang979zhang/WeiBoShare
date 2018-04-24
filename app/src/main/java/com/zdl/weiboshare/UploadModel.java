package com.zdl.weiboshare;

/**
 * Created by Administrator on 2018/4/24.
 */

class UploadModel {


    /**
     * data : {"audioLength":"string","originalUrl":"string","thumbnailUrl":"string","videoPicuture":"string"}
     * message : string
     * response : string
     */

    private DataBean data;
    private String message;
    private String response;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public static class DataBean {
        /**
         * audioLength : string
         * originalUrl : string
         * thumbnailUrl : string
         * videoPicuture : string
         */

        private String audioLength;
        private String originalUrl;
        private String thumbnailUrl;
        private String videoPicuture;

        public String getAudioLength() {
            return audioLength;
        }

        public void setAudioLength(String audioLength) {
            this.audioLength = audioLength;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getVideoPicuture() {
            return videoPicuture;
        }

        public void setVideoPicuture(String videoPicuture) {
            this.videoPicuture = videoPicuture;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "audioLength='" + audioLength + '\'' +
                    ", originalUrl='" + originalUrl + '\'' +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    ", videoPicuture='" + videoPicuture + '\'' +
                    '}';
        }
    }
}
