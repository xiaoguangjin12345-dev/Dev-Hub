package com.xgj.devpulse.mapper;

import com.xgj.devpulse.pojo.dto.notice.NoticeQueryDTO;
import com.xgj.devpulse.pojo.entity.NoticeEntity;
import com.xgj.devpulse.pojo.vo.notice.NoticeVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoticeMapper {

    // 添加消息通知
    @Insert("""
            insert into `Notice`
                (`RecieverID`, `SenderID`, `Title`, `Content`,
                 `NoticeType`, `Status`, `CreateTime`)
            values
                (#{RecieverID}, #{SenderID}, #{Title}, #{Content},
                 #{NoticeType}, #{Status}, #{CreateTime})
            """)
    int insertNotice(NoticeEntity notice);

    // 执行消息通知逻辑删除时，更新状态字段
    @Update("update `Notice` set `Status` = #{status} where `NoticeID` = #{noticeId}")
    int updateDeleteInfo(@Param("noticeId") Integer noticeId,
                         @Param("status") Byte status);


    // 获取消息通知列表
    List<NoticeVO> getNoticesByReceiverID(@Param("role") Byte role,
                                          @Param("receiverId") Integer receiverId,
                                          @Param("dto") NoticeQueryDTO dto,
                                          @Param("offset") Integer offset,
                                          @Param("size") Integer size);

    // 获取消息通知列表总数
    long countNoticesByReceiverID(@Param("role") Byte role,
                                  @Param("receiverId") Integer receiverId,
                                  @Param("dto") NoticeQueryDTO dto);



    // 根据消息通知编号，获取特定消息通知信息
    NoticeVO getNoticeById(@Param("role") Byte role,
                           @Param("receiverId") Integer receiverId,
                           @Param("noticeId") Integer noticeId);

    // 根据编号，查询消息通知实体
    @Select("select * from `Notice` where `NoticeID` = #{noticeId}")
    NoticeEntity getNoticeEntity(@Param("noticeId") Integer noticeId);

    // 获取未读消息总数
    @Select("select count(*) from `Notice` where `RecieverID` = #{receiverId} and `Status` = #{status}")
    Integer getUnreadNoticeCount(@Param("receiverId") Integer receiverId,
                                 @Param("status") Byte status);

}
