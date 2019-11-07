package com.joycastle.gamepluginbase;

import java.util.HashMap;

/**
 * Created by geekgy on 16/4/22.
 */
public interface AnalyticDelegate extends LifeCycleDelegate {
    /**
     * 设置账户信息
     * @param map map
     */
    public void setAccoutInfo(HashMap<String, Object> map);

    /**
     * 自定义事件
     * @param eventId eventId
     */
    public void onEvent(String eventId);

    /**
     * 自定义事件
     * @param eventId eventId
     * @param eventLabel eventLabel
     */
    public void onEvent(String eventId, String eventLabel);

    /**
     * 自定义事件
     * @param eventId eventId
     * @param eventData eventData
     */
    public void onEvent(String eventId, HashMap<String, String> eventData);

    /**
     * 设置等级
     * @param level level
     */
    public void setLevel(int level);

    /**
     * 充值
     * @param iapId iapId
     * @param cash cash
     * @param coin coin
     * @param channal channal
     */
    public void charge(String iapId, double cash, double coin, int channal);

    /**
     * 奖励
     * @param coin coin
     * @param reason reason
     */
    public void reward(double coin, int reason);

    /**
     * 购买
     * @param good good
     * @param amount amount
     * @param coin coin
     */
    public void purchase(String good, int amount, double coin);

    /**
     * 使用
     * @param good good
     * @param amount amount
     * @param coin coin
     */
    public void use(String good, int amount, double coin);

    /**
     * 开始任务
     * @param missionId missionId
     */
    public void onMissionBegin(String missionId);

    /**
     * 任务达成
     * @param missionId missionId
     */
    public void onMissionCompleted(String missionId);

    /**
     * 任务失败
     * @param missionId missionId
     * @param reason reason
     */
    public void onMissionFailed(String missionId, String reason);

}
