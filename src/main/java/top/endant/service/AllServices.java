package top.endant.service;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class AllServices {

    @Autowired
    private MyWebDriver myWebDriver;

    public void closePagesButLeftOne() {
        String[] windowHandles = myWebDriver.getWindowHandles().toArray(new String[0]);
        for (int i = windowHandles.length - 1; i > 0; i--) {
            myWebDriver.switchTo().window(windowHandles[i]);
            myWebDriver.close();
        }
        myWebDriver.switchTo().window(windowHandles[0]);
    }

    public List<String> testSwitch() throws InterruptedException {
        List<String> r = new ArrayList<>();
        //默认为bilibili
        myWebDriver.get("https://www.bilibili.com");
        Thread.sleep(100);
        r.add("跳转至bilibili");
        r.add(testMaximize());
        return r;
    }

    public String testSwitch(String url) throws InterruptedException {

        myWebDriver.get(url);
        Thread.sleep(100);
        String title = myWebDriver.getTitle();
        return "跳转至" + title + testMaximize();

    }

    public String testMaximize() {
        myWebDriver.manage().window().maximize();
        return "最大化窗口";
    }

    public boolean testGetCookie() {
        //设置cookie
        try {
            File cookieFile = new File("BiliBiliCookie.txt");
            FileReader fileReader = new FileReader(cookieFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
                while (stringTokenizer.hasMoreTokens()) {

                    String name = stringTokenizer.nextToken();
                    String value = stringTokenizer.nextToken();
                    String domain = stringTokenizer.nextToken();
                    String path = stringTokenizer.nextToken();
                    Date expiry = null;
                    String dt;

                    if (!(dt = stringTokenizer.nextToken()).equals("null")) {
                        expiry = new Date(dt);
                    }

                    boolean isSecure = Boolean.parseBoolean(stringTokenizer.nextToken());
                    Cookie cookie = new Cookie(name, value, domain, path, expiry, isSecure);
                    myWebDriver.manage().addCookie(cookie);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String testSaveCookie() {
        StringBuilder res = new StringBuilder();
        //cookie 操作保存
        File cookieFile = new File("BiliBiliCookie.txt");
        try {
            res.append("尝试保存cookie");
            cookieFile.delete();
            cookieFile.createNewFile();
            FileWriter fileWriter = new FileWriter(cookieFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Cookie cookie : myWebDriver.manage().getCookies()) {
                bufferedWriter.write((cookie.getName() + ";" +
                        cookie.getValue() + ";" +
                        cookie.getDomain() + ";" +
                        cookie.getPath() + ";" +
                        cookie.getExpiry() + ";" +
                        cookie.isSecure()));
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            res.append("保存cookie遇到错误");
            e.printStackTrace();
        }
        return res.toString();
    }

    public boolean testIsLogin() {
        WebElement loginButton = myWebDriver.findElement(By.className("header-login-entry"));
        return loginButton == null;
    }

    public String testLogin() {
        StringBuilder res = new StringBuilder();
        //登录操作
        //获取登录按钮
        try {
            WebElement loginButton = myWebDriver.findElement(By.className("header-login-entry"));
            loginButton.click();
            res.append("登录按钮点击");
            Thread.sleep(1000);
            //使用QQ登录，确保已经登录QQ
            List<WebElement> loginIcons = myWebDriver.findElements(By.className("login-sns-item-icon"));
            loginIcons.get(2).click();
            res.append("QQ头像点击");
            Thread.sleep(1000);

            try {
                //尝试正常定位元素没找到，注意到iframe的存在，而driver只能在一个页面内找元素
                myWebDriver.switchTo().frame(myWebDriver.findElement(By.id("ptlogin_iframe")));

                WebElement loginQQ = myWebDriver.findElement(By.className("face"));
                loginQQ.click();
                res.append("登录成功");
                Thread.sleep(100);
            } catch (Exception e) {
                res.append("登录失败，页面关闭，请重试或检查");
                e.printStackTrace();
                Thread.sleep(100);
                //关闭页面
                myWebDriver.close();
                return res.toString();
            }

            //登录完成虽然页面跳转了，但是driver没有有同步，手动回到页面
            myWebDriver.get("https://www.bilibili.com");
            Thread.sleep(1000);
            res.append("回到bilibili首页");
            return res.toString();
        } catch (Exception e) {
            res.append("已经通过cookie登录，或者登录遇到错误");
            return res.toString();
        }
    }

    public String testSearch(String keyWord) throws InterruptedException {
        WebElement input = myWebDriver.findElement(By.className("nav-search-input"));
        input.sendKeys(keyWord + "\r\n");
        Thread.sleep(1000);
        String[] windowHandles = myWebDriver.getWindowHandles().toArray(new String[0]);
        myWebDriver.switchTo().window(windowHandles[1]);
        String title = myWebDriver.getTitle();

        return "搜索" + keyWord + "并切换到搜索页" + title;
    }

    //默认播放搜索的第一个视频
    public String testPlay() throws InterruptedException {
        StringBuilder res = new StringBuilder();

        List<WebElement> videoCards = myWebDriver.findElements(By.xpath("//div[contains(@class,'bili-video-card__image')]"));
        videoCards.get(0).click();
        Thread.sleep(100);
        res.append("点击第一个视频播放3s");
        String[] windowHandles = myWebDriver.getWindowHandles().toArray(new String[0]);
        myWebDriver.switchTo().window(windowHandles[2]);
        String title = myWebDriver.getTitle();
        res.append("当前播放的视频标题是").append(title);
        Thread.sleep(3000);

        return res.toString();
    }

    //播放首页的第i个视频
    public String testPlay(int i) throws InterruptedException {
        StringBuilder res = new StringBuilder();

        List<WebElement> videoCards = myWebDriver.findElements(By.xpath("//div[contains(@class,'bili-video-card__image')]"));
        videoCards.get(i).click();
        Thread.sleep(100);
        res.append("点击bilibili首页第").append(i).append("个视频，播放3s");

        String[] windowHandles = myWebDriver.getWindowHandles().toArray(new String[0]);
        myWebDriver.switchTo().window(windowHandles[1]);
        String title = myWebDriver.getTitle();
        res.append("当前播放的视频标题是").append(title);
        Thread.sleep(3);

        return res.toString();
    }

    public String testClickHistory() throws InterruptedException {
        StringBuilder res = new StringBuilder();

        List<WebElement> icons = myWebDriver.findElements(By.className("right-entry-text"));
        icons.get(4).click();
        Thread.sleep(1000);
        String[] windowHandles = myWebDriver.getWindowHandles().toArray(new String[0]);
        myWebDriver.switchTo().window(windowHandles[1]);

        res.append("点击历史记录按钮，并切换到搜索页");
        return res.toString();
    }

    public String testClickDelete(int i) throws Exception {
        StringBuilder res = new StringBuilder();
        List<WebElement> icons = myWebDriver.findElements(By.xpath("//i[contains(@class,'history-delete')]"));
        icons.get(i - 1).click();
        Thread.sleep(1000);

        res.append("删除第").append(i).append("条历史记录");
        return res.toString();
    }

    public String testGiveALike() throws InterruptedException {
        StringBuilder res = new StringBuilder();
        WebElement like = myWebDriver.findElement(By.xpath("//div[contains(@class,'video-like')]"));
        like.click();
        Thread.sleep(1000);

        res.append("为视频点赞");
        return res.toString();
    }

    public String testGiveCoins(int i) throws InterruptedException {
        StringBuilder res = new StringBuilder();
        WebElement coin = myWebDriver.findElement(By.xpath("//div[contains(@class,'video-coin')]"));
        coin.click();
        Thread.sleep(500);
        List<WebElement> coinPut = myWebDriver.findElements(By.className("coin-run-box"));
        coinPut.get(i - 1).click();
        Thread.sleep(1000);
        WebElement confirm = myWebDriver.findElement(By.className("bi-btn"));
        confirm.click();
        Thread.sleep(1000);
        res.append("为视频投").append(i).append("个币");
        return res.toString();
    }
}
