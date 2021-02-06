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
    private final static String USER_NAME = "53291283F";
    private final static String USER_PASS = "28032020";
    private final static LocalDate NEXTDAY = LocalDate.now().plusDays(2);
    private final static DayOfWeek DAYOFWEEK = NEXTDAY.getDayOfWeek();
    private final static String NEXTDAYFORMATTED = NEXTDAY.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Method: Main");
        new Thread(() -> findReservation(UserType.PAOLA, CenterType.MIRIBILLA, SportType.SWIMMING)).start();
        new Thread(() -> findReservation(UserType.LEO, CenterType.MIRIBILLA, SportType.SWIMMING)).start();
        new Thread(() -> findReservation(UserType.TEO, CenterType.MIRIBILLA, SportType.SWIMMING)).start();

    }
    private static void findReservation(UserType user, CenterType centerType, SportType sportType){
        //Create a new drive
        LOGGER.info("Method: Main " + user + centerType + sportType );
        System.setProperty("webdriver.chrome.driver", "/home/leonardo/Downloads/chromedriver_linux64/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        LOGGER.info("Open login page");
        driver.get(URL_LOGIN);
        LOGGER.info("Setting userName and Password");
        WebElement loginText = driver.findElement(By.name("codigo"));
        loginText.sendKeys(USER_NAME);
        WebElement passText = driver.findElement(By.name("clave"));
        passText.sendKeys(USER_PASS);
        WebElement submitButton = driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[2]/div/button"));
        submitButton.click();
        LOGGER.info("Open reservation page");
        if(UserType.PAOLA == user){
            driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_horas.jsp?codCom="+ centerType.getValue() + "&codAct="+ sportType.getValue() +"&numAut=&fechaReserva=" + NEXTDAYFORMATTED);
        }else{
            driver.get("https://www.bilbaokirolak.com/bkonline2/cambio_usuario.jsp?familiar=" + user.getValue());
            driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_horas.jsp?codCom="+ centerType.getValue() + "&codAct="+ sportType.getValue() +"&numAut=&fechaReserva=" + NEXTDAYFORMATTED);
        }
        LOGGER.info("Find all ul");
        List<WebElement> webElements = driver.findElements(By.xpath("/html/body/div[1]/div[15]/div[2]/div[1]/ul//li/a[contains(@href,'bkonline2')]"));
        for (WebElement element : webElements) {
              String[] hourText = element.getText().split("-");
            if(hourText[0].trim().equals(DAYOFWEEK.getValue() > 4? "11:00" : "12:00")) {
                //call reservation page
                if (openReservation(element, driver))
                        break;
            }
        }
        driver.close();
    }

    private static boolean openReservation(WebElement element, WebDriver driver){
        LOGGER.info("Method: openReservation " + element.getText());

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.open('"+element.getAttribute("href")+"')");

        String parentWindow = driver.getWindowHandle();
        Set<String> handles =  driver.getWindowHandles();
        for(String windowHandle  : handles)
        {
            if(!windowHandle.equals(parentWindow))
            {
                driver.switchTo().window(windowHandle);
                // press button ok
                WebElement reservationButton = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/form/div[2]/div/button"));
                reservationButton.click();
                // reservation confirmation
                WebElement reservationConfirmationButton = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/form/div/div/a[2]"));
                reservationConfirmationButton.click();
                //ALERT CONFIRMATION
                //Alert alert = driver.switchTo().alert();
                //alert.accept(); // for OK
                // modal confirmation
                WebElement reservationModalConfirmationButton = driver.findElement(By.xpath("/html/body/div[1]/div[32]/div/div/div/a[2]"));
                reservationModalConfirmationButton.click();
                WebElement errorMesage = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/div/h3"));
                if(errorMesage.getText().equals("No se ha podido guardar su solicitud")){
                    LOGGER.info("Reservation not found");
                    driver.close(); //closing child window
                    driver.switchTo().window(parentWindow); //switch to parent window
                    return false;
                }else {
                    LOGGER.info("Reservation has been saved" );
                    //parent close
                    driver.close();
                    driver.switchTo().window(parentWindow);
                    return true;
                }
            }
        }
        return false;
    }
}
