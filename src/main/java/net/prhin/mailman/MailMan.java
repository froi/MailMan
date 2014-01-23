
/**********************************************************************************************************************
 * Copyright 2014 Froilan Irizarry                                                                                    *
 * http://froilanirizarry.me                                                                                          *
 * https://github.com/froi                                                                                            *
 *                                                                                                                    *
 * Code can be downloaded, forked, or revied at:                                                                      *
 * 	https://github.com/froi/MailMan                                                                                   *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not                                        *
 * use this file except in compliance with the License. You may obtain a copy of                                      *
 * the License at:                                                                                                    *
 * 	http://www.apache.org/licenses/LICENSE-2.0                                                                        *
 * Unless required by applicable law or agreed to in writing, software                                                *
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT                                          *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the                                           *
 * License for the specific language governing permissions and limitations under                                      *
 * the License.                                                                                                       *
 **********************************************************************************************************************/

package net.prhin.mailman;

import org.apache.commons.io.IOUtils;


import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by froilan on 14-1-18.
 */
public class MailMan {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("mailman");

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty(resourceBundle.getString("mailman.mail.store"), resourceBundle.getString("mailman.protocol"));

        Session session = Session.getInstance(props);

        try {

            Store store = session.getStore();
            store.connect(resourceBundle.getString("mailman.host"), resourceBundle.getString("mailman.user"),
                    resourceBundle.getString("mailman.password"));
            Folder inbox = store.getFolder(resourceBundle.getString("mailman.folder"));
            inbox.open(Folder.READ_ONLY);
            inbox.getUnreadMessageCount();
            Message[] messages = inbox.getMessages();

            for(int i = 0; i <= messages.length / 2; i++) {
                Message tmpMessage = messages[i];

                Multipart multipart = (Multipart) tmpMessage.getContent();

                System.out.println("Multipart count: " + multipart.getCount());

                for (int j = 0; j < multipart.getCount(); j++) {
                    BodyPart bodyPart = multipart.getBodyPart(j);

                    if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                        if(bodyPart.getContent().getClass().equals(MimeMultipart.class)) {
                            MimeMultipart mimeMultipart = (MimeMultipart)bodyPart.getContent();

                            for(int k = 0; k < mimeMultipart.getCount(); k++) {
                                if(mimeMultipart.getBodyPart(k).getFileName() != null) {
                                    printFileContents(mimeMultipart.getBodyPart(k));
                                }
                            }
                        }
                    } else {
                        printFileContents(bodyPart);
                    }
                }
            }

            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void printFileContents(BodyPart bodyPart) throws IOException, MessagingException {
        InputStream is = bodyPart.getInputStream();

        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(is, stringWriter);
        System.out.println("File Content: " + stringWriter.toString());
    }
}
