package com.microservice.beans;

/**
 * @author zhangwei
 * @date 2019-12-04
 * <p>
 * 1.自定义响应状态码 10000-不设上限，以操作类型为单元区分 默认区间 100
 * 2.API对接文档输出参照枚举
 */
public enum HttpStatusMicro {

    /**
     * 数据库操作错误 10000-10099
     */
    SQL_SELECT_ERROR(10000, "服务器异常, 查询数据失败"),
    SQL_UPDATE_ERROR(10001, "服务器异常, 修改数据失败"),
    SQL_UPDATE_BATH_ERROR(10002, "服务器异常, 批量修改数据失败"),
    SQL_INSERT_ERROR(10003, "服务器异常, 添加数据失败"),
    SQL_INSERT_BATH_ERROR(10004, "服务器异常, 批量添加数据失败"),
    SQL_DELETE_ERROR(10005, "服务器异常, 移除数据失败"),
    SQL_DELETE_BATH_ERROR(10006, "服务器异常, 批量移除数据失败"),
    SQL_NOEXISTS_ERROR(10007, "未查询到信息"),

    /**
     * BODY参数错误 10100-10199
     */
    BEAN_FULLNULL_ERROR(10100, "参数全为空值"),
    BEAN_EXISTSNULL_ERROR(10101, "参数存在空值"),
    BEAN_PK_ID_NOTNULL_ERROR(10102, "参数编号必须为空"),
    BEAN_PK_ID_ISNULL_ERROR(10103, "参数编号不能为空"),
    BEAN_EXISTED_ERROR(10104, "参数已存在"),
    BEAN_ANALYSIS_ERROR(10105, "参数解析错误"),
    BEAN_LAWFUL_OR_EXISTSNULL_ERROR(10106, "参数不合法或者存在空值"),
    BEAN_NOT_EXISTED_ERROR(10107, "参数不存在"),
    BEAN_STATUS_VALUE_ERROR(10108, "参数状态值错误"),
    BEAN_SYSTEM_MODULE_NOTMATCH_ERROR(10109, "系統和模块参数不匹配"),

    /**
     * QUERY参数错误 10200-10299
     */
    REQUERT_PARAMETER_ERROR(10200, "请求参数错误或者不合法"),
    REQUERT_PARAMETER_EXISTS(10200, "请求参数已存在"),
    REQUERT_PARAMETER_NULL_ERROR(10201, "请求参数为空值"),
    REQUERT_PARAMETER_EXISTS_NULL_ERROR(10202, "请求参数存在空值"),


    /**
     * 基础常用提示错误 10300-10399
     */
    USER_NOT_FOUND_ERROR(10300, "用户不存在"),
    USER_NEED_AUTHORITIES(10301, "用户未登录"),
    USER_LOGIN_FAILED(10302, "用户账号或密码错误"),
    USER_LOGIN_SUCCESS(10303, "用户登录成功"),
    USER_NO_ACCESS(10304, "用户无权访问"),
    USER_LOGOUT_SUCCESS(10305, "用户登出成功"),
    USER_LOGIN_RESPONSE_NULL_ERROR(10306, "登录响应参数为空"),
    USER_LOGIN_IS_OVERDUE(10307, "登录已失效"),

    //TOKEN_IS_BLACKLIST(10308, "此token为黑名单"),
    //TOKEN_NOT_FOUND(10309, "token不存在"),
    TOKEN_INVALIDATE(10310, "token无效"),

    GATEWAY_NOT_ALLOW(10312, "非网关认证不允许请求"),

    REDIS_RUNTIME_ERROR(10313, "缓存错误"),


    RESPONSE_DATA_NULL_ERROR(10314, "查询结果不存在"),

    SERVER_FEIGN_UC_API_ERROR(10315, "微服务feign调用uc模块API错误"),
    SERVER_FEIGN_WORKFLOW_API_ERROR(10316, "微服务feign调用workflow模块API错误"),
    SERVER_FEIGN_CMDB_API_ERROR(10317, "微服务feign调用cmdb模块API错误"),
    SERVER_FEIGN_TROUBLEREPORT_API_ERROR(10318, "微服务feign调用troublereport模块API错误"),
    SERVER_GENERATE_ID_ERROR(10319, "生成分布式ID失败"),
    USER_LOGIN_ERROR(10320, "服务器内部错误, 登录失败"),
    INTERNAL_SERVER_ERROR(10321, "服务器内部错误"),

    /**
     * 用户中心错误类型 uc-sever 10400-11399
     */
    USER_EXISTED_ERROR(10401, "该用户已存在"),
    GROUP_OR_MEMBERS_NOEXISTED_ERROR(10402, "组或成员不存在"),
    GROUP_OR_ROLE_NOEXISTED_ERROR(10403, "组或角色不存在"),
    ROLE_EXISTED_ERROR(10403, "该角色已存在"),
    ROLE_NOEXISTED_ERROR(10404, "该角色不存在"),
    USER_UN_SUB_ACCOUNT_USED_ERROR(10405, "该帐号已禁用,待启用"),
    USER_UN_MAIN_ACCOUNT_USED_ERROR(10406, "主帐号已禁用,待启用"),

    USERNAME_NOT_NULL(10407, "登录用户名不能为空"),
    PASSWORD_NOT_NULL(10408, "登录密码不能为空"),
    SYSTEM_NOT_NULL(10409, "登录系统不能为空"),
    SYSTEM_NOT_FIT(10410, "登录系统不匹配"),
    USERNAME_NOTONLY(10411, "用户名存在不唯一字段"),
    REGISTER_ERROR(10412, "注册失败,信息不全"),

    USER_SYSTEM_NOT_NULL(10413, "用户所属系统不能为空"),
    USER_ROLES_NOT_NULL(10414, "用户未分配角色"),
    USERNAME_EMAIL_SYSID_ID_EXISTEDNULL(10415, "用户名、邮箱、系统名或id为空"),
    SYSID_NULL_ERROR(10416, "系统名为空"),
    ROLE_DATA_ERROR(10417, "角色数据异常"),
    PERMISSION_DATA_ERROR(10418, "权限数据异常"),
    USERNAME_OR_EMAIL_EXSISTED(10419, "用户名或邮箱已存在"),
    USER_FIND_ERROR(10420, "用户查询失败"),
    USER_REGISTER_FAIL(10421, "用户注册失败"),

    DEPARTMENT_PARENT_NOT_NULL(10422, "部门的父级部门ID不能为空"),
    USER_ROLE_RELATE_USER(10423, "该角色具有绑定的用户，请确认是否删除！"),
    USER_ROLE_RELATE_GROUP(10424, "该角色具有绑定的用户组，请确认是否删除！"),
    PASSWORD_OLD_PASSWORD_NOT_AGREEMENT_ERROR(10425, "原密码错误！"),
    PASSWORD_NEW_PASSWORD_NOT_AGREEMENT_ERROR(10426, "新密码与确认密码不一致！"),
    PASSWORD_OLD_NEW_PASSWORD_AGREEMENT_ERROR(10427, "原密码与新密码一致！"),
    PASSWORD_CHANGE_ERROR(10428, "服务器内部错误,修改密码失败！"),
    USER_ALREADY_LOGIN_ERROR(10429, "该用户已登录"),
    USER_WECHAT_LOGIN_OAUTH_ERROR(10430,"解析微信企业号用户信息为空！"),
    USER_MANAGER_CENTER_USERID_NULL(14431,"对应用户管理平台认证传递的用户管理平台对应的用户id为空！"),
    USER_MANAGER_CENTER_CROSS_WINTOKEN_NULL(14432,"用户管理平台调用报障系统回传地址，传入winToken为空！"),
    USER_MANAGER_CENTER_CROSS_VERIFY_ERROR(14433,"跨站验证请求用户管理平台失败!"),
    USER_PASSWORD_MODIFY_NO_AUTHORIZE(14434,"您无权限修改非本人的用户账号密码！"),
    USER_MANAGER_CENTER_CROSS_VERIFY_TOKEN_NULL(14435,"跨站验证请求用户管理平台获取token为空!"),
    USER_ITSM_LOGIN_OAUTH_ERROR(14436,"解析itsm平台token信息为空!"),
    USER_ITSM_LOGIN_OAUTH_FAILED(14437,"当前用户无报障系统对应权限，请联系管理员！"),

    /**
     * 调度中心错误 scheduler-server 11400-12399
     */


    /**
     * CMDB系统错误 cmdb-sever 12400-13399
     */
    SYSTEM_OR_MODULE_NO_EXISTED_ERROR(12400, "系统或模块不存在"),
    SYSTEM_AND_MODULE_EXISTED_ERROR(12401, "系统和模块组合已存在"),
    SYSTEM_CODE_EXISTED_ERROR(12402, "系统代码已存在"),
    MODULES_NOT_FOUND_ERROR(12403, "未查询到系统对应模块信息"),
    SYSTEM_NO_CONFIG_NOT_FOUND_ERROR(12404, "未发现系统编码配置"),

    /**
     * 报障模块错误 troublereport-server 13400-14399
     */
    TROUBLE_REPROT_ORDER_TEMP_SUCCESS(13400, "报障工单保存草稿成功"),
    TROUBLE_REPROT_ORDER_TEMP_ERROR(13401, "报障工单保存草稿失败"),
    TROUBLE_REPROT_ORDER_SAVE_SUCCESS(13402, "报障工单提交成功"),
    TROUBLE_REPROT_ORDER_SAVE_ERROR(13403, "报障工单提交失败"),

    TROUBLE_REPORE_DISPATCH_ERROR(13405, "报障自动分派失败, [组长、组员]均未发现"),
    TROUBLE_REPORE_CREATE_FAULT_ERROR(13406, "报障生成任务单失败"),
    TROUBLE_REPORE_ORGAN_HAND_ERROR(13407, "机构系统管理员处理失败"),
    TROUBLE_REPORE_REPEAT_ERROR(13408, "管理员重复操作拒绝处理"),

    TROUBLE_REPORE_NOT_FOUND_ERROR(13409, "查询报障单不存在,或者不在当前环节"),
    TROUBLE_REPORE_HANDLE_EMPTY_ERROR(13410, "处理信息不完整或为空"),
    TROUBLE_REPORE_HANDLE_SQL_ERROR(13411, "处理报障工单时保存数据异常"),
    TROUBLE_REPORE_BASICIFNOS_ERROR(13412, "查询报障单信息失败"),

    TROUBLE_REPORE_HANDLE_DETAIL_ERROR(13414, "[处理结果,帮助意见,转补充资料]必填一项"),
    TROUBLE_REPORE_TRANSFERREASON_ERROR(13415, "报障单转派原因不能为空"),
    TROUBLE_REPORE_NUMBER_NULL_ERROR(13416, "报障单号码为空"),
    TROUBLE_REPORE_ROUTE_NO_EXISTE_ERROR(13417, "报障单信息不存在"),
    TROUBLE_REPORE_SYSTEM_NOT_ALLOW_REPORT_ERROR(13418, "IT系统管理组层级不可上报"),
    TROUBLE_REPORE_IT_SYSTEM_CROSS_OR_APART_ALLOW_ERROR(13419, "IT系统管理组才允许[跨组转派/拆单/挂起]"),
    TROUBLE_REPORE_IT_SYSTEM_ADMIN_CROSS__OR_APART_ALLOW_ERROR(13420, "IT系统管理组组长才允许[跨组转派/拆单]"),
    TROUBLE_REPORE_IT_SYSTEM_CROSS_AND_APART_ALLOW_ERROR(13421, "[跨组转派]不可与[拆单]同时选择"),
    TROUBLE_REPORE_IT_SYSTEM_LIST_NO_DATA_ERROR(13422, "IT系统管理组查询不到"),
    TROUBLE_REPORE_TASK_APART_COUNT_LESS_ERROR(13423, "拆单失败, 拆单数量少于目标数量"),

    TROUBLE_REPORT_NO_DUPLICATE_UTGE(13419, "存在24小时之内的催单记录，请稍后再进行催单"),
    TROUBLE_REPORT_URGE_MESSAGE(13420, "调用外部接口发送邮件或微信失败"),
    TROUBLE_REPORT_URGE_FAILD(13421, "报障任务催办失败"),
    TROUBLE_REPORT_URGE_BEAN_DTO_FALIED(13422, "实体与对象转换失败"),
    TROUBLE_REPORT_BASICINFO_TEMP_EXISTS_ERROR(13423, "报障单草稿已存在"),
    TROUBLE_REPORT_BASICINFO_SAVE_EXISTS_ERROR(13424, "报障单提交已存在"),
    TROUBLE_REPORT_HANDLE_DETAIL_NOT_FOUND_ERROR(13425, "报障处理单未找到"),
    TROUBLE_REPORT_HANDLE_TYPE_NOT_FOUND_ERROR(13426, "当前处理类型只能是:[组员/长处理,组员帮助意见,自验]"),
    TROUBLE_REPORT_HANDLE_TYPE_NOT_ALLOW_ERROR(13427, "当前待办单,未匹配到待办处理类型"),
    TROUBLE_REPORT_SAVE_HELP_DESC_ERROR(13428, "添加帮助意见失败"),
    TROUBLE_REPORT_HANDLE_ERROR(13429, "报障单提交处理结果失败"),
    TROUBLE_REPORT_TEMP_LEAVE_PARAM_NOT_NULL(13430, "暂离开始或结束时间不能为空"),
    TROUBLE_REPORT_TEMP_LEAVE_DUPLICATE_TIME(13431, "此时间段内含有已提交的暂离申请数据，请确认"),
    TROUBLE_REPORT_TEMP_LEAVE_SAVE_FIALD(134232, "暂离单保存失败"),
    TROUBLE_REPORT_FILE_SAVE_FIALD(134233, "文件保存或更新失败"),
    TROUBLE_REPORT_HEAD_OFFICE_GROUP_NOT_FOUND(134234, "对应系统总公司系统管理组不存在"),
    TROUBLE_REPORT_HEAD_OFFICE_ADMIN_NOT_FOUND(134235, "对应系统总公司系统管理员不存在"),
    TROUBLE_REPORT_PROBLEM_FAILD(134236, "问题件数据保存或更新失败"),
    TROUBLE_REPORT_PROBLEM_NOT_FOUND(134237, "问题件数据不存在"),
    TROUBLE_REPORT_PROBLEM_SAVE_ERROR(134238, "问题件数据保存失败"),
    TROUBLE_REPORT_BASICINFO_APPEND_SAVE_ERROR(134239, "报障单提交补充资料失败"),
    TROUBLE_REPORT_DIC_UPDATE_ERROR(134240, "字典信息无对应数据，无法更新"),
    TROUBLE_REPORT_RELATE_BASIC_ID_NULL(134241, "报障追加信息关联的基本报障信息id不能为空"),
    TROUBLE_REPORT_CALENDAR_GENERATE_FAIL(134242, "生成对应年份所在的工作日历数据失败"),
    TROUBLE_REPORT_CALENDAR_GENERATE_YEAR_NULL(134243, "请求参数对应的年份或月份不能为空"),
    TROUBLE_REPORT_FIND_YEAR_NULL(134244, "查询对应年份数据失败"),
    TROUBLE_REPORT_GENERATE_WORK_FLOW_ERROR(134245, "构建工作流参数失败,没有实现构建参数方法"),
    TROUBLE_REPORT_SYNCHRONIZED_SYSTEM_MODULE_ERROR(134246, "跨组转派, 同步系统模块失败"),
    TROUBLE_REPORT_REVOKE_REPORT_ERROR(134247, "撤销报障单失败"),

    /**
     * 工作流模块错误 workflow-server 14400- 15399
     */
    WORKFLOW_ERROR(14406, "工作流异常:"),
    WORKFLOW_MODE_DEFINE_SUCCESS(14400, "流程模型定义成功"),
    WORKFLOW_MODE_DEFINE_ERROR(14401, "流程模型定义失败"),
    WORKFLOW_START_INSTANCE_ERROR(14402, "流程实例启动失败"),
    WORKFLOW_COMPLETE_INSTANCE_ERROR(14403, "完成流程实例任务失败"),
    WORKFLOW_COMPLETE_SUBTASK_NOT_ALL_COMPLETED_ERROR(14404, "子任务没有全部完成,无法提交主流程任务!"),
    WORKFLOW_PARAM_NOT_COMPLETELY_ERROR(14405, "流程变量参数异常或不完整,请检查参数!"),


    TROUBLE_REPORT_USER_LIST_ERROR(15501, "用户列表获取失败"),
    TROUBLE_REPORT_USER_INFO_ERROR(15502, "获取用户信息失败"),
    TROUBLE_REPORT_USER_EMAIL_ERROR(15503, "用户邮箱未知"),

    // 角色相关
    TROUBLE_REPORT_ROLES_NULL_ERROR(15504, "机构角色列表获取失败"),

    //组织机构相关
    TROUBLE_REPORT_ORGAN_INFO_FAILD(15505, "组织机构获取失败！"),
    TROUBLE_REPORT_ORGAN_ADD_FAILD(15506, "组织机构操作失败！"),
    TROUBLE_REPORT_DEPT_INFO_FAILD(15507, "部门机构获取失败！"),
    TROUBLE_REPORT_DEPT_ADD_FAILD(15508, "部门操作失败！"),

    TROUBLE_REPORT_ROLES_STATUS_ERROR(15509, "修改角色状态失败！"),
    TROUBLE_REPORT_ORGAN_EXIST_FAILD(15510, "该组织机构已存在！"),
    TROUBLE_REPORT_DEPT_EXIST_FAILD(15511, "该公司下的部门已存在！"),
    TROUBLE_REPORT_ORGAN_NULL_EXIST_FAILD(15512, "该公司不存在！"),

    // 用户管理相关
    TROUBLE_REPORT_PREMISSION_GET_ERROR(15513, "用户权限获取失败"),
    TROUBLE_REPORT_PREMISSION_SELECT_ERROR(15514, "查询权限失败"),
    TROUBLE_REPORT_PREMISSION_SELECT_NULL(15515, "没有查询到满足条件的权限"),
    TROUBLE_REPORT_DEPT_HAVE_SUB_INFO_NOT_DELETE(15516, "此部门下含有子部门，不允许删除！"),
    TROUBLE_REPORT_DEPT_HAVE_USER_INFO_NOT_DELETE(15517, "此部门下含有用户，不允许删除！"),
    TROUBLE_REPORT_ORGAN_HAVE_DEPT_NOT_DELETE(15518, "此机构下含有部门，不允许删除！"),
    TROUBLE_REPORT_ORGAN_HAVE_ORGAN_NOT_DELETE(15519, "此机构下含有子机构，不允许删除！"),
    TROUBLE_REPORT_DEPT_NOT_EXIST(15520, "没有查询到删除条件id对应的部门信息！"),

    //用户组
    GROUP_NOT_EXIST(16000, "用户组不存在"),
    GROUP_DESC_EXIST(16001, "项目组名已存在，请尝试其他项目组名称！"),
    GROUP_CODE_EXIST(16002, "当前组代码已存在！"),
    GROUP_ROLES_NOT_NULL(16003, "当前组没有分配角色!"),
    GROUP_ROLES_ADD_UPDATE_NULL(16004, "当前组新增或更新角色失败!"),
    GROUP_USERS_NOT_NULL(16005, "当前组没有分配用户!"),
    GROUP_CODE_NOT_NULL(16006, "请求参数组代码不能为空!"),
    GROUP_FLAG_NOT_NULL(16007, "请求参数组类型标识不能为空!"),
    GROUP_NOT_RELATE_SYSTEM(16008, "当前组没有关联系统，请确认!"),
    GROUP_SYSTEM_EXIST(16009, "所选系统已存在项目组，请勿重复添加项目组！"),

    /**
     * fc系统,17000-17999
     */
    FILE_SYSTEM_UPLOAD_FILE_NULL(17000, "请选择上传的文件"),
    FILE_SYSTEM_UPLOAD_SYSID_NULL(17001, "上传文件来源系统不能为空"),
    FILE_SYSTEM_SAVE_DETAIL_ERROR(17002, "保存文件信息到详情表失败"),
    FILE_SYSTEM_SAVE_HISTORY_ERROR(17003, "保存文件信息到历史表失败"),
    FILE_SYSTEM_DELETE_ERROR(17004, "文件信息删除失败"),
    FILE_SYSTEM_DELETE_BY_ID_NULL(17005, "文件信息删除失败"),
    FILE_SYSTEM_DELETE_IDS_NULL(17006, "删除的id集合为空！"),
    FILE_SYSTEM_FIND_ID_NULL(17007, "查询的文件对应的在FC库中的文件id不能为空！"),
    FILE_SYSTEM_UPLOAD_ERROR(17008, "上传文件时出现异常"),
    FILE_SYSTEM_FILE_NOT_EXIST(17009, "未找到要下载的文件"),
    FILE_SYSTEM_DOWNLOAD_ERROR(17010, "文件下载异常"),
    FILE_SYSTEM_FILE_SEARCH_ERROR(17011, "文件查询异常");


    private int code;
    private String message;

    HttpStatusMicro(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
