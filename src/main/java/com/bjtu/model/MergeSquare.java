package com.bjtu.model;

import java.util.List;

/**
 * 合并后的区域
 * 
 * @author apple
 */
public class MergeSquare {
    // 区域 id
    private String id;
    // 合并区域集合
    private List<Square> mergeSquare;
    // 合并区域点的个数
    private Long count;
    // 合并区域属于的簇 id
    private String canopyId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Square> getMergeSquare() {
        return mergeSquare;
    }

    public void setMergeSquare(List<Square> mergeSquare) {
        this.mergeSquare = mergeSquare;
    }

    public Long getCount() {
        long result = 0;
        for (Square square : mergeSquare) {
            result = result + square.getCount();
        }
        return result;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getCanopyId() {
        return canopyId;
    }

    public void setCanopyId(String canopyId) {
        this.canopyId = canopyId;
    }

    @Override
    public String toString() {
        return "MergeSquare [id=" + id + ", mergeSquare=" + mergeSquare + ", count=" + count + ", canopyId=" + canopyId
                + "]";
    }
}
