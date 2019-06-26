package ijmo.demo.springboard.message;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class MessageValidator implements Validator {

    private final String REQUIRED = "required";
    private boolean isTitleRequired;

    public MessageValidator(boolean isTitleRequired) {
        this.isTitleRequired = isTitleRequired;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Message message = (Message) target;
        String title = message.getTitle();
        String body = message.getBody();

        if (isTitleRequired && StringUtils.isEmpty(title)) {
            errors.rejectValue("title", REQUIRED, "Title is required.");
        }

        if (StringUtils.isEmpty(body)) {
            errors.rejectValue("body", REQUIRED, "Body is required.");
        }
    }
}
