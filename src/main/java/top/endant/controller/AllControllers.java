package top.endant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.endant.service.AllServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test-web")
@CrossOrigin
public class AllControllers {

    @Autowired
    private AllServices allServices;

    @PostMapping("/switch")
    public ResultDto<List<String>> testSwitch(@RequestBody Map<String, String> map) {
        List<String> r = new ArrayList<>();
        try {
            String url = map.get("url");
            String s = allServices.testSwitch(url);
            r.add(s);
            return ResultDto.success(r);
        } catch (InterruptedException e) {
            return ResultDto.error(e.getMessage());
        }
    }

    @PostMapping("/search")
    public ResultDto<List<String>> testSearch(@RequestBody Map<String, String> map) {
        List<String> r = new ArrayList<>();
        try {
            String key = map.get("key");
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            //确保在bilibili首页
            List<String> strings = allServices.testSwitch();
            r.add(strings.get(0));
            r.add(strings.get(1));
            r.add(allServices.testSearch(key));
            return ResultDto.success(r);
        } catch (Exception e) {
            return ResultDto.error(e.getMessage());
        }
    }

    @PostMapping("/play")
    public ResultDto<List<String>> testPlay(@RequestBody Map<String, String> map) {
        List<String> r = new ArrayList<>();
        try {
            String index = map.get("index");
            int i = Integer.parseInt(index);
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            //确保在bilibili首页
            List<String> strings = allServices.testSwitch();
            r.add(strings.get(0));
            r.add(strings.get(1));
            r.add(allServices.testPlay(i));
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            return ResultDto.success(r);
        } catch (Exception e) {
            return ResultDto.error(e.getMessage());
        }
    }


    @PostMapping("/test1")
    public ResultDto<List<String>> test1(@RequestBody Map<String, String> map) {
        List<String> r = new ArrayList<>();
        try {
            String key = map.get("key");
            System.out.println(key);
            login(r);

            //搜索播放操作
            if (key == null) key = "听书人";
            System.out.println(key);
            r.add(allServices.testSearch(key));
            r.add(allServices.testPlay());
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            return ResultDto.success(r);
        } catch (Exception e) {
            return ResultDto.error(e.getMessage());
        }
    }

    @PostMapping("/test2")
    public ResultDto<List<String>> test2(@RequestBody Map<String, String> map) {
        List<String> r = new ArrayList<>();
        try {
            String index = map.get("index");
            int i = Integer.parseInt(index);

            login(r);

            r.add(allServices.testClickHistory());
            r.add(allServices.testClickDelete(i));
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            return ResultDto.success(r);
        } catch (Exception e) {
            return ResultDto.error(e.getMessage());
        }
    }

    @PostMapping("/test3")
    public ResultDto<List<String>> test3(@RequestBody Map<String, String> map) {
        List<String> r = new ArrayList<>();
        try {
            int i = Integer.parseInt(map.get("index"));
            int like = Integer.parseInt(map.get("like")); //0,1
            int coins = Integer.parseInt(map.get("coins")); //0,1,2

            login(r);

            r.add(allServices.testPlay(i));
            if (like != 0) r.add(allServices.testGiveALike());
            if (coins != 0) {
                r.add(allServices.testGiveCoins(coins));
                if (like == 0) allServices.testGiveALike(); //取消点赞
                Thread.sleep(3000);
            }
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            return ResultDto.success(r);
        } catch (Exception e) {
            return ResultDto.error(e.getMessage());
        }
    }

    @PostMapping("/testWithData")
    public ResultDto<List<String>> testWithData(@RequestBody Map<String, List<String>> map) {
        List<String> r = new ArrayList<>();
        try {
            List<String> keys = map.get("keys");
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            //确保在bilibili首页
            List<String> strings = allServices.testSwitch();
            r.add(strings.get(0));
            r.add(strings.get(1));
            for (String key : keys) {
                r.add(allServices.testSearch(key));
                r.add(allServices.testPlay());
                //关闭至只剩一个页面
                allServices.closePagesButLeftOne();
                allServices.testSwitch();
                Thread.sleep(1000);
            }
            return ResultDto.success(r);
        } catch (Exception e) {
            return ResultDto.error(e.getMessage());
        }
    }

    public void login(List<String> r) {
        try {
            //关闭至只剩一个页面
            allServices.closePagesButLeftOne();
            //确保在bilibili首页
            List<String> strings = allServices.testSwitch();
            r.add(strings.get(0));
            r.add(strings.get(1));
            //登录操作
            if (allServices.testGetCookie()) {
                r.add("获取cookie成功");
                allServices.testSwitch();
            }
            if (!allServices.testIsLogin()) {
                r.add("cookie登录失败，改为第三方登录");
                r.add(allServices.testLogin());
                r.add(allServices.testSaveCookie());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
