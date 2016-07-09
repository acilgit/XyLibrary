package com.test.baserefreshview;

import java.io.Serializable;
import java.util.List;

/**
 * Created by XY on 2016/7/8.
 */
public class ListBean implements Serializable {

    /**
     * resultCode : 1
     * message : 二手房列表
     * content : {"content":[{"id":"5bfea27f-2798-411c-bf12-6f3f6e741f77","villageName":"陈锐东的小区","title":"1","secondTitle":"1","price":1,"address":"","houseSize":100,"houseShape":null,"tags":"精品房","coverPicture":"http://192.168.1.222:9000/upload/image/20160707/1467884769053068717.jpg"},{"id":"a20eb416-d018-4c48-85e4-90f54007a8a2","villageName":"新基地","title":"100","secondTitle":"100","price":100,"address":"东莞南城","houseSize":100,"houseShape":"三室一厅","tags":"学期房","coverPicture":""},{"id":"57316be2-cd2c-4097-85d4-1363deb69a01","villageName":"未来世界花园","title":"有房出售","secondTitle":"出售","price":100,"address":"未来花园","houseSize":1,"houseShape":null,"tags":"学区房,精品房","coverPicture":"http://localhost:8082/upload/image/20160705/1467712586557091084.jpg"},{"id":"28801fc9-7d1a-429a-9747-b007f12cdaff","villageName":"未来世界花园","title":"未来世界花园急售","secondTitle":"急售","price":10000,"address":"东莞市南城新机电","houseSize":120,"houseShape":"四室二厅","tags":"精品房,学区房,电梯房,学期房","coverPicture":"http://192.168.1.233:9000/upload/image/20160705/1467707503209032491.jpg"},{"id":"323bcb9f-6639-4591-81c1-5c3b6c8ce18c","villageName":"未来世界花园","title":"三房两厅","secondTitle":"舒适宜人","price":11160,"address":"东莞市南城区石竹宏图大道科技大道三元路","houseSize":112,"houseShape":"三室二厅","tags":"精品房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616920219062420.jpg"},{"id":"bd343f6d-6dff-4fa2-b976-d327fb8f9f64","villageName":"未来世界花园","title":"采光好","secondTitle":"户型极佳","price":16666,"address":"东莞市凤冈镇凤平大道华侨中学旁","houseSize":75,"houseShape":"二室一厅","tags":"电梯房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616705001021495.jpg"},{"id":"7e68431c-e5df-43e1-9317-5670c34acdd8","villageName":"未来世界花园","title":"全新装修","secondTitle":"送车位","price":14136,"address":"东莞市长安镇河东二路71号","houseSize":133,"houseShape":"三室二厅","tags":"电梯房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616456818091723.jpg"},{"id":"70c860e5-8637-4a51-846f-741fa29cb8e9","villageName":"测试","title":"东城地铁房","secondTitle":"精品房","price":13541,"address":"东莞市东城区环成东路交汇处","houseSize":96,"houseShape":"三室二厅","tags":"精品房,学期房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616154176065582.jpg"},{"id":"04285a94-5a4d-4809-8b26-4a50826a52e9","villageName":"未来世界花园","title":"首付5万","secondTitle":"名校在旁","price":9107,"address":"东莞市南城区东坑骏达中路","houseSize":56,"houseShape":"二室二厅","tags":"学区房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467615702343040703.jpg"},{"id":"cf5e1b64-70fa-4b3a-baf2-eec712b48238","villageName":"未来世界花园","title":"精装3房","secondTitle":"送车位","price":11363,"address":"东莞市万江区莞穗大道318号","houseSize":132,"houseShape":"三室一厅","tags":"精品房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467615480981007316.jpg"},{"id":"242c8817-0611-4b5e-a451-fcb87518cd5c","villageName":"未来世界花园","title":"11","secondTitle":"11","price":11,"address":"","houseSize":1,"houseShape":null,"tags":"精品房","coverPicture":""}],"number":0,"size":200,"sort":null,"numberOfElements":11,"totalPages":1,"lastPage":true,"firstPage":true,"totalElements":11}
     */

    private int resultCode;
    private String message;
    /**
     * content : [{"id":"5bfea27f-2798-411c-bf12-6f3f6e741f77","villageName":"陈锐东的小区","title":"1","secondTitle":"1","price":1,"address":"","houseSize":100,"houseShape":null,"tags":"精品房","coverPicture":"http://192.168.1.222:9000/upload/image/20160707/1467884769053068717.jpg"},{"id":"a20eb416-d018-4c48-85e4-90f54007a8a2","villageName":"新基地","title":"100","secondTitle":"100","price":100,"address":"东莞南城","houseSize":100,"houseShape":"三室一厅","tags":"学期房","coverPicture":""},{"id":"57316be2-cd2c-4097-85d4-1363deb69a01","villageName":"未来世界花园","title":"有房出售","secondTitle":"出售","price":100,"address":"未来花园","houseSize":1,"houseShape":null,"tags":"学区房,精品房","coverPicture":"http://localhost:8082/upload/image/20160705/1467712586557091084.jpg"},{"id":"28801fc9-7d1a-429a-9747-b007f12cdaff","villageName":"未来世界花园","title":"未来世界花园急售","secondTitle":"急售","price":10000,"address":"东莞市南城新机电","houseSize":120,"houseShape":"四室二厅","tags":"精品房,学区房,电梯房,学期房","coverPicture":"http://192.168.1.233:9000/upload/image/20160705/1467707503209032491.jpg"},{"id":"323bcb9f-6639-4591-81c1-5c3b6c8ce18c","villageName":"未来世界花园","title":"三房两厅","secondTitle":"舒适宜人","price":11160,"address":"东莞市南城区石竹宏图大道科技大道三元路","houseSize":112,"houseShape":"三室二厅","tags":"精品房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616920219062420.jpg"},{"id":"bd343f6d-6dff-4fa2-b976-d327fb8f9f64","villageName":"未来世界花园","title":"采光好","secondTitle":"户型极佳","price":16666,"address":"东莞市凤冈镇凤平大道华侨中学旁","houseSize":75,"houseShape":"二室一厅","tags":"电梯房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616705001021495.jpg"},{"id":"7e68431c-e5df-43e1-9317-5670c34acdd8","villageName":"未来世界花园","title":"全新装修","secondTitle":"送车位","price":14136,"address":"东莞市长安镇河东二路71号","houseSize":133,"houseShape":"三室二厅","tags":"电梯房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616456818091723.jpg"},{"id":"70c860e5-8637-4a51-846f-741fa29cb8e9","villageName":"测试","title":"东城地铁房","secondTitle":"精品房","price":13541,"address":"东莞市东城区环成东路交汇处","houseSize":96,"houseShape":"三室二厅","tags":"精品房,学期房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467616154176065582.jpg"},{"id":"04285a94-5a4d-4809-8b26-4a50826a52e9","villageName":"未来世界花园","title":"首付5万","secondTitle":"名校在旁","price":9107,"address":"东莞市南城区东坑骏达中路","houseSize":56,"houseShape":"二室二厅","tags":"学区房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467615702343040703.jpg"},{"id":"cf5e1b64-70fa-4b3a-baf2-eec712b48238","villageName":"未来世界花园","title":"精装3房","secondTitle":"送车位","price":11363,"address":"东莞市万江区莞穗大道318号","houseSize":132,"houseShape":"三室一厅","tags":"精品房","coverPicture":"http://192.168.1.233:9000/upload/image/20160704/1467615480981007316.jpg"},{"id":"242c8817-0611-4b5e-a451-fcb87518cd5c","villageName":"未来世界花园","title":"11","secondTitle":"11","price":11,"address":"","houseSize":1,"houseShape":null,"tags":"精品房","coverPicture":""}]
     * number : 0
     * size : 200
     * sort : null
     * numberOfElements : 11
     * totalPages : 1
     * lastPage : true
     * firstPage : true
     * totalElements : 11
     */

    private Content content;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public static class Content {
        private int number;
        private int size;
        private Object sort;
        private int numberOfElements;
        private int totalPages;
        private boolean lastPage;
        private boolean firstPage;
        private int totalElements;
        /**
         * id : 5bfea27f-2798-411c-bf12-6f3f6e741f77
         * villageName : 陈锐东的小区
         * title : 1
         * secondTitle : 1
         * price : 1.0
         * address :
         * houseSize : 100.0
         * houseShape : null
         * tags : 精品房
         * coverPicture : http://192.168.1.222:9000/upload/image/20160707/1467884769053068717.jpg
         */

        private List<ContentBean> content;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public Object getSort() {
            return sort;
        }

        public void setSort(Object sort) {
            this.sort = sort;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isLastPage() {
            return lastPage;
        }

        public void setLastPage(boolean lastPage) {
            this.lastPage = lastPage;
        }

        public boolean isFirstPage() {
            return firstPage;
        }

        public void setFirstPage(boolean firstPage) {
            this.firstPage = firstPage;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public static class ContentBean {
            private String id;
            private String villageName;
            private String title;
            private String secondTitle;
            private double price;
            private String address;
            private double houseSize;
            private Object houseShape;
            private String tags;
            private String coverPicture;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getVillageName() {
                return villageName;
            }

            public void setVillageName(String villageName) {
                this.villageName = villageName;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSecondTitle() {
                return secondTitle;
            }

            public void setSecondTitle(String secondTitle) {
                this.secondTitle = secondTitle;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public double getHouseSize() {
                return houseSize;
            }

            public void setHouseSize(double houseSize) {
                this.houseSize = houseSize;
            }

            public Object getHouseShape() {
                return houseShape;
            }

            public void setHouseShape(Object houseShape) {
                this.houseShape = houseShape;
            }

            public String getTags() {
                return tags;
            }

            public void setTags(String tags) {
                this.tags = tags;
            }

            public String getCoverPicture() {
                return coverPicture;
            }

            public void setCoverPicture(String coverPicture) {
                this.coverPicture = coverPicture;
            }
        }
    }
}
