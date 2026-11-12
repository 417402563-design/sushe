package cn.edu.sdua.sushe.config;

import cn.edu.sdua.sushe.entity.*;
import cn.edu.sdua.sushe.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 演示数据初始化：首次启动时写入内置数据库的种子数据。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepo;
    @Autowired private RoomRepository roomRepo;
    @Autowired private RepairOrderRepository orderRepo;
    @Autowired private SparePartRepository partRepo;
    @Autowired private KnowledgeArticleRepository knowledgeRepo;
    @Autowired private NoticeRepository noticeRepo;
    @Autowired private FeedbackRepository feedbackRepo;
    @Autowired private ScheduleRepository scheduleRepo;
    @Autowired private EvaluationRepository evaluationRepo;
    @Autowired private MessageRepository messageRepo;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) {
            return; // 已初始化
        }

        // ===== 用户 =====
        User admin = user("admin", "123456", "系统管理员", "ADMIN", "13800000000");
        User w1 = user("worker1", "123456", "张水电", "WORKER", "13800000001");
        w1.setSkills("水电,灯具,插座");
        User w2 = user("worker2", "123456", "李木工", "WORKER", "13800000002");
        w2.setSkills("家具,门窗,锁具");
        User w3 = user("worker3", "123456", "王网络", "WORKER", "13800000003");
        w3.setSkills("网络,家电,弱电");
        User s1 = user("stu1", "123456", "陈同学", "STUDENT", "13900000001");
        s1.setCollege("计算机科学与技术学院 2022-1");
        User s2 = user("stu2", "123456", "刘同学", "STUDENT", "13900000002");
        s2.setCollege("机电工程学院 2023-2");
        userRepo.saveAll(List.of(admin, w1, w2, w3, s1, s2));

        // ===== 宿舍 =====
        List<Room> rooms = new ArrayList<>();
        rooms.add(room("1号楼", 1, "101", "四人间", 4, "男", "住满"));
        rooms.add(room("1号楼", 1, "102", "四人间", 4, "男", "住满"));
        rooms.add(room("1号楼", 2, "201", "四人间", 4, "男", "空闲"));
        rooms.add(room("2号楼", 1, "101", "六人间", 6, "女", "住满"));
        rooms.add(room("2号楼", 3, "305", "六人间", 6, "女", "维修中"));
        roomRepo.saveAll(rooms);

        // ===== 工单 =====
        RepairOrder o1 = order(s1, rooms.get(0), "水电", "宿舍卫生间漏水",
                "卫生间地面持续渗水，疑似水管破裂，急需维修。", "紧急", "PROCESSING", w1);
        o1.setAcceptTime(LocalDateTime.now().minusHours(2));
        RepairOrder o2 = order(s2, rooms.get(3), "家具", "书桌抽屉损坏",
                "抽屉滑轨脱落，无法正常开关。", "普通", "PENDING", null);
        RepairOrder o3 = order(s1, rooms.get(1), "网络", "宿舍网口无信号",
                "102房间靠窗网口插网线无反应。", "普通", "DONE", w3);
        o3.setAcceptTime(LocalDateTime.now().minusDays(1));
        o3.setFinishTime(LocalDateTime.now().minusHours(3));
        o3.setWorkHours(1.5);
        o3.setFinishRemark("更换网口模块，测试网络恢复正常。");
        orderRepo.saveAll(List.of(o1, o2, o3));

        // ===== 备件库存 =====
        partRepo.saveAll(List.of(
                part("LED灯管", "电料", 120, "根", 30, 15.0),
                part("PVC水管接头", "水暖", 18, "个", 20, 8.5),
                part("五孔插座", "电料", 60, "个", 20, 12.0),
                part("抽屉滑轨", "五金", 40, "副", 15, 9.0),
                part("网线模块(RJ45)", "弱电", 9, "个", 20, 5.0),
                part("门锁锁芯", "五金", 25, "个", 10, 22.0)
        ));

        // ===== 知识库 =====
        knowledgeRepo.saveAll(List.of(
                kb("宿舍网络无法连接的自查步骤", "网络",
                        "1.检查网线是否插紧；2.查看路由器指示灯；3.尝试重启电脑网卡；4.仍无法解决请在系统中报修。",
                        "网络 无法上网 网线 掉线"),
                kb("卫生间漏水应急处理", "水电",
                        "先关闭进水阀门，避免积水蔓延，然后立即提交紧急报修，拍照上传现场。",
                        "漏水 水阀 卫生间 应急"),
                kb("灯具不亮如何判断", "电料",
                        "若个别灯不亮多为灯管损坏；整间房间无电需检查空气开关是否跳闸。",
                        "灯 不亮 跳闸 电"),
                kb("书桌家具类报修说明", "家具",
                        "滑轨、铰链等小五金损坏可由维修工现场更换，报修时请附照片。",
                        "书桌 抽屉 滑轨 家具")
        ));

        // ===== 公告 =====
        Notice n1 = notice("关于宿舍报修流程升级的通知",
                "即日起紧急报修支持图片上传与智能识别，请同学们如实填写故障描述并上传现场照片。", "系统管理员", true);
        Notice n2 = notice("五一假期维修值班安排",
                "假期期间每日安排一名维修工轮值，普通报修顺延至假期后处理。", "系统管理员", false);
        noticeRepo.saveAll(List.of(n1, n2));

        // ===== 反馈/投诉 =====
        Feedback fb = new Feedback();
        fb.setStudentId(s2.getId());
        fb.setStudentName(s2.getRealName());
        fb.setType("OPINION");
        fb.setContent("希望可以增加晚间报修的响应速度。");
        fb.setStatus("PENDING");
        feedbackRepo.save(fb);

        // ===== 排班 =====
        String today = LocalDateTime.now().toLocalDate().toString();
        scheduleRepo.saveAll(List.of(
                sch(w1, today, "早班", 8.0),
                sch(w2, today, "中班", 8.0),
                sch(w3, today, "晚班", 6.0)
        ));

        // ===== 评价（针对已完成工单） =====
        Evaluation ev = new Evaluation();
        ev.setOrderId(o3.getId());
        ev.setStudentId(s1.getId());
        ev.setStudentName(s1.getRealName());
        ev.setWorkerId(w3.getId());
        ev.setWorkerName(w3.getRealName());
        ev.setScore(5);
        ev.setSpeedScore(5);
        ev.setQualityScore(4);
        ev.setAttitudeScore(5);
        ev.setComment("师傅来得很快，网络问题一次解决，服务态度好。");
        evaluationRepo.save(ev);
        w3.setAvgRating(5.0);
        w3.setFinishedCount(1);
        w3.setTotalWorkHours(1.5);
        userRepo.save(w3);

        // ===== 消息 =====
        Message m = new Message();
        m.setToUserId(s1.getId());
        m.setFromUserId(admin.getId());
        m.setType("SYSTEM");
        m.setContent("欢迎使用智慧校园宿舍维修管理系统！");
        messageRepo.save(m);

        System.out.println(">>> 内置数据库初始化完成，演示账号：admin/123456，worker1/123456，stu1/123456");
    }

    // ---------- 构造辅助 ----------
    private User user(String uname, String pwd, String name, String role, String phone) {
        User u = new User();
        u.setUsername(uname);
        u.setPassword(pwd);
        u.setRealName(name);
        u.setRole(role);
        u.setPhone(phone);
        u.setStatus(1);
        return u;
    }

    private Room room(String building, int floor, String no, String type, int cap, String gender, String status) {
        Room r = new Room();
        r.setBuilding(building);
        r.setFloor(floor);
        r.setRoomNo(no);
        r.setDormType(type);
        r.setCapacity(cap);
        r.setGenderLimit(gender);
        r.setStatus(status);
        return r;
    }

    private RepairOrder order(User s, Room r, String category, String title, String desc,
                              String urgency, String status, User worker) {
        RepairOrder o = new RepairOrder();
        o.setStudentId(s.getId());
        o.setStudentName(s.getRealName());
        o.setRoomId(r.getId());
        o.setBuilding(r.getBuilding());
        o.setRoomNo(r.getRoomNo());
        o.setCategory(category);
        o.setTitle(title);
        o.setDescription(desc);
        o.setUrgency(urgency);
        o.setStatus(status);
        if (worker != null) {
            o.setWorkerId(worker.getId());
            o.setWorkerName(worker.getRealName());
        }
        return o;
    }

    private SparePart part(String name, String cat, int stock, String unit, int safety, double price) {
        SparePart p = new SparePart();
        p.setPartName(name);
        p.setCategory(cat);
        p.setStock(stock);
        p.setUnit(unit);
        p.setSafetyStock(safety);
        p.setPrice(price);
        return p;
    }

    private KnowledgeArticle kb(String title, String cat, String content, String kw) {
        KnowledgeArticle a = new KnowledgeArticle();
        a.setTitle(title);
        a.setCategory(cat);
        a.setContent(content);
        a.setKeywords(kw);
        return a;
    }

    private Notice notice(String title, String content, String author, boolean top) {
        Notice n = new Notice();
        n.setTitle(title);
        n.setContent(content);
        n.setAuthor(author);
        n.setTop(top);
        return n;
    }

    private Schedule sch(User w, String date, String shift, double hours) {
        Schedule s = new Schedule();
        s.setWorkerId(w.getId());
        s.setWorkerName(w.getRealName());
        s.setWorkDate(date);
        s.setShift(shift);
        s.setHours(hours);
        s.setStatus("待值班");
        return s;
    }
}
