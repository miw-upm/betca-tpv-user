package es.upm.miw.betca_tpv_user.domain.services.mail;

import es.upm.miw.betca_tpv_user.TestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestConfig
public class MailServiceTest {

    @MockBean
    private MailSender mailSender;

    @Autowired
    private MailService mailService;

    /*@Test
    public void testSendMessage() {
        final ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        this.mailService.send("test@gmail.com", "test message");
        verify(mailSender).send(captor.capture());
        assertThat(mailService.getFrom(), is(captor.getValue().getFrom()));
    }*/
}
