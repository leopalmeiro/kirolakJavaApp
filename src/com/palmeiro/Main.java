package com.palmeiro;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class Main {


    private final static String URL_BASE = "https://www.bilbaokirolak.com/";
    private final static String URL_LOGIN = URL_BASE + "bkonline2/doLogin.jsp";
    private final static String URL_RESERVATION = URL_BASE + "bkonline2/reservas/reservar_pago_finalizado.jsp?";
    private final static String BKTS_PAOLA = URL_BASE + "1612303550121";
    private final static LocalDate NEXTDAY = LocalDate.now().plusDays(4);
    private final static DayOfWeek DAYOFWEEK = NEXTDAY.getDayOfWeek();
    private final static String NEXTDAYFORMATTED = NEXTDAY.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Method: Main");
        System.setProperty("webdriver.chrome.driver", "/home/leonardo/Downloads/chromedriver_linux64/chromedriver");

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        LOGGER.info("Open login page");
        driver.get(URL_LOGIN);

        LOGGER.info("Setting userName and Password");
        WebElement loginText = driver.findElement(By.name("codigo"));
        loginText.sendKeys("53291283F");
        WebElement passText = driver.findElement(By.name("clave"));
        passText.sendKeys("28032020");
        WebElement submitButton = driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[2]/div/button"));
        submitButton.click();
        List<User> userList = new ArrayList<>();
        userList.add(new User("paola", "&bkts=1612297919452"));

        userList.add(new User("leo", "familiar=198824&bkts=1612297941831"));

        userList.add(new User("teo", "familiar=198825&bkts=1612297974445s"));


        for (int i = 0; i < userList.size(); i++) {
            LOGGER.info("Open reservation page");
            if(userList.get(i).getName().equals("paola")){
                driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_horas.jsp?codCom=14&codAct=26&numAut=&fechaReserva=" + NEXTDAYFORMATTED + userList.get(i).getUrl());
            }else{
                driver.get("https://www.bilbaokirolak.com/bkonline2/cambio_usuario.jsp?" + userList.get(i).getUrl());
                driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_horas.jsp?codCom=14&codAct=19&numAut=&fechaReserva=" + NEXTDAYFORMATTED + "&" + userList.get(i).getUrl());


            }
            LOGGER.info("Find all ul");
            //WebElement ulElement = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/div[1]/ul"));
            //List<WebElement> liTags = ulElement.findElements(By.tagName("li"));
            //LOGGER.info("Find all li " + liTags.size());
            List<WebElement> webElements = driver.findElements(By.xpath("/html/body/div[1]/div[15]/div[2]/div[1]/ul//li/a[contains(@href,'bkonline2')]"));
            for (WebElement element : webElements) {

                LOGGER.info("Find ahref from li " + element.getText());
                String[] hourText = element.getText().split("-");
                if(hourText[0].trim().equals(DAYOFWEEK.getValue() > 4? "11:00" : "18:00")) {
                    //call reservation page
                    openReservation(element, driver);

                }
            }
        }
        driver.close();
    }

    private static void openReservation(WebElement element, WebDriver driver){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.open('"+element.getAttribute("href")+"')");

        String currentWindow = driver.getWindowHandles();
        Set<String> allWindows = driver.getWindowHandles();
        Iterator<String> i = allWindows.iterator();
        driver.switchTo().window(i);
        //newWindow.get("https://blog.testproject.io/");

///html/body/div[1]/div[15]/div[2]/form/div[2]/div/button
        // press button ok
        WebElement reservationButton = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/form/div[2]/div/button"));
        reservationButton.click();
        // reservation confirmation
        WebElement reservationConfirmationButton = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/form/div/div/a[2]"));
        reservationConfirmationButton.click();
        // modal confirmation
        WebElement reservationModalConfirmationButton = driver.findElement(By.xpath("/html/body/div[1]/div[32]/div/div/div/a[2]"));
        reservationModalConfirmationButton.click();
        WebElement errorMesage = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/div/h3"));
        if(errorMesage.getText() !="No se ha podido guardar su solicitud"){
            LOGGER.info("Open reservation page again");
            driver.navigate().back();
            driver.navigate().back();
            driver.navigate().back();
        }else {
            LOGGER.info("Reservation has been saved");
            //TODO add click on save
            driver.close();
        }
    }
}
