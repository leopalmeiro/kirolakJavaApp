package com.palmeiro;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class Main {


    private static final String URL_BASE = "https://www.bilbaokirolak.com/";
    private static final String URL_LOGIN = URL_BASE + "bkonline2/doLogin.jsp";
    private static final String USER_NAME = "53291283F";
    private static final String USER_PASS = "28032020";
    private static final LocalDate NEXTDAY = LocalDate.now().plusDays(2);
    private static final DayOfWeek DAYOFWEEK = NEXTDAY.getDayOfWeek();
    private static final String NEXTDAYFORMATTED = NEXTDAY.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Method: Main");
        new Thread(() -> findReservation(UserType.PAOLA, CenterType.ATXURI, SportType.SWIMMING)).start();
        new Thread(() -> findReservation(UserType.LEO, CenterType.ATXURI, SportType.SWIMMING)).start();
        new Thread(() -> findReservation(UserType.TEO, CenterType.ATXURI, SportType.SWIMMING)).start();

    }

    private static void findReservation(UserType user, CenterType centerType, SportType sportType) {
        //Create a new drive
        LOGGER.info("Method: findReservation " + user + " " + centerType + " " + sportType);
        System.setProperty("webdriver.chrome.driver", "/home/leonardo/Downloads/chromedriver_linux64/chromedriver");

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        //driver.manage().window().maximize();
        LOGGER.info("Open login page");
        driver.get(URL_LOGIN);
        LOGGER.info("Setting userName and Password");
        WebElement loginText = driver.findElement(By.name("codigo"));
        loginText.sendKeys(USER_NAME);
        WebElement passText = driver.findElement(By.name("clave"));
        passText.sendKeys(USER_PASS);
        WebElement submitButton = driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[2]/div/button"));
        submitButton.click();
        LOGGER.info("Open reservation page " +user);
        if (UserType.PAOLA == user) {
            driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_horas.jsp?codCom=" + centerType.getValue() + "&codAct=" + sportType.getValue() + "&numAut=&fechaReserva=" + NEXTDAYFORMATTED);
        } else {
            driver.get("https://www.bilbaokirolak.com/bkonline2/cambio_usuario.jsp?familiar=" + user.getValue());
            driver.get("https://www.bilbaokirolak.com/bkonline2/reservas/reservar_horas.jsp?codCom=" + centerType.getValue() + "&codAct=" + sportType.getValue() + "&numAut=&fechaReserva=" + NEXTDAYFORMATTED);
        }
        List<WebElement> webElements = driver.findElements(By.xpath("/html/body/div[1]/div[15]/div[2]/div[1]/ul//li/a[contains(@href,'bkonline2')]"));
        for (WebElement element : webElements) {
            LOGGER.info(element.getAttribute("href"));
            String[] hourText = element.getText().split("-");
            if (hourText[0].trim().equals(DAYOFWEEK.getValue() > 5 ? "11:00" : "16:00")) {
                //call reservation page
                if (!checkElementIsDisabled(element)) {
                    if (openReservation(element, driver, user))
                        break;
                }
            }

        }
        driver.close();
    }

    private static boolean checkElementIsDisabled(WebElement element) {
        LOGGER.info("Method: checkElementIsDisabled ");
        return element.getAttribute("class").contains("ui-state-disabled");
    }

    private static boolean openReservation(WebElement element, WebDriver driver, UserType userType) {
        LOGGER.info("Method: openReservation at: " + element.getText() + "User: " + userType);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.open('" + element.getAttribute("href") + "')");

        String parentWindow = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String windowHandle : handles) {
            if (!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                // press button ok
                WebElement reservationButton = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/form/div[2]/div/button"));
                reservationButton.click();
                // reservation confirmation
                WebElement reservationConfirmationButton = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/form/div/div/a[2]"));
                reservationConfirmationButton.click();
                //ALERT CONFIRMATION
                // modal confirmation
                WebDriverWait wait = new WebDriverWait(driver, 2);

                WebElement reservationModalConfirmationButton = driver.findElement(By.xpath("/html/body/div[1]/div[32]/div/div/div/a[2]"));
                wait.until(ExpectedConditions.elementToBeClickable(reservationModalConfirmationButton));

                reservationModalConfirmationButton.click();
                WebElement errorMesage = driver.findElement(By.xpath("/html/body/div[1]/div[15]/div[2]/div/h3"));
                LOGGER.info("Error message " + errorMesage);
                if (errorMesage.getText().equals("No se ha podido guardar su solicitud")) {
                    LOGGER.info("Reservation error " + userType);
                    driver.close(); //closing child window
                    driver.switchTo().window(parentWindow); //switch to parent window
                    return false;
                } else {
                    LOGGER.info("Reservation has been saved " + userType);
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
