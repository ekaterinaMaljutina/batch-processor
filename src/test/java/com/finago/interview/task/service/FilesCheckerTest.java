package com.finago.interview.task.service;

import com.finago.interview.task.BatchProcessor;
import com.finago.interview.task.event.FindFilesEvent;
import com.finago.interview.task.model.ReceiverItem;
import com.finago.interview.task.model.Receivers;
import com.finago.interview.task.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import static com.finago.interview.task.utils.Utils.IN_FOLDER;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest({Utils.class})
@PowerMockIgnore({"javax.xml.*", "com.sun.xml.*", "org.xml.*" })
@ContextConfiguration(classes = BatchProcessor.Factory.class)
public class FilesCheckerTest extends PowerMockTestCase {

    @Autowired
    private FilesChecker filesChecker;

    @Mock
    private Utils utils;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkErrorXML() {
        String xml = "<?xml version = \"1.0\" encoding = \"UTF-8\" standalone = \"no\" ?>\n" +
                "<receivers>\n" +
                "    <receiver>\n" +
                "        <receiver_id>8842</receiver_id>\n" +
                "        <first_name>Martin</first_name>\n" +
                "        <last_name>van Nostrand</last_name>\n" +
                "        <file>220019901.pdf</file>\n" +
                "        <file_md5>213dcefaeb5d50ceb7de1be998c02032</file_md5>\n" +
                "    </receiver>";
        String errorXML = "2200199110.xml";
        createXML(xml, errorXML);

        filesChecker.onApplicationEvent(new FindFilesEvent(this, Collections.singleton(errorXML)));
        Assert.assertTrue(Utils.checkExistFile(Utils.ERROR_FOLDER + errorXML));
    }

    @Test
    public void checkMissingPDF() {
        Receivers receivers = createReceivers(142994, "First", "Last", "0000000.pdf", "");
        PowerMockito.stub(PowerMockito.method(Utils.class, "readXML")).toReturn(receivers);

        filesChecker.onApplicationEvent(new FindFilesEvent(this, Collections.singleton("sample.xml")));
        Assert.assertTrue(Utils.checkExistFile(Utils.ERROR_FOLDER + "94/142994/0000000.xml"));
    }

    @Test
    public void checkCorruptedMd5() {
        Receivers receivers = createReceivers(8842, "", "", "90072657.pdf",
                "3ec56d15c9366a5045f1de688867f74a");
        PowerMockito.stub(PowerMockito.method(Utils.class, "readXML")).toReturn(receivers);

        filesChecker.onApplicationEvent(new FindFilesEvent(this, Collections.singleton("sample.xml")));
        Assert.assertTrue(Utils.checkExistFile(Utils.ERROR_FOLDER + "42/8842/90072657.pdf"));
        Assert.assertTrue(Utils.checkExistFile(Utils.ERROR_FOLDER + "42/8842/90072657.xml"));
    }

    @Test
    public void checkCorrectPDF() {

        Receivers receivers = createReceivers(8841, "", "", "90072657.pdf",
                "3ec56d15c9366a5045f5de688867f74a");
        PowerMockito.stub(PowerMockito.method(Utils.class, "readXML")).toReturn(receivers);

        filesChecker.onApplicationEvent(new FindFilesEvent(this, Collections.singleton("sample.xml")));
        Assert.assertTrue(Utils.checkExistFile(Utils.OUT_FOLDER + "41/8841/90072657.pdf"));
        Assert.assertTrue(Utils.checkExistFile(Utils.OUT_FOLDER + "41/8841/90072657.xml"));
    }

    private static void createXML(String xml, String fileName) {
        File file = new File(IN_FOLDER + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Assert.fail();
        }
        try (FileOutputStream outputStream = new FileOutputStream(IN_FOLDER + fileName)) {
            outputStream.write(xml.getBytes());
        } catch (IOException e) {
            Assert.fail();
        }
    }

    private static Receivers createReceivers(int id, String firstName, String lastName, String fileName, String md5) {
        Receivers receivers = new Receivers();
        receivers.addReceiver(new ReceiverItem(id, firstName, lastName, fileName, md5));
        return receivers;
    }


}
