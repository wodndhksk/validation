package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        log.info("objectName={}", errors.getObjectName());

        /**
         * 아래 if 문을 통해 구현한 결과와 같은 편리한 유틸리티인 ValidationUtils.
         * 단, 다음과 같이 한줄로 가능, 제공하는 기능은 Empty , 공백 같은 단순한 기능만 제공
         */
//        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");

        /**
         * errorCode 에 'required'만 있어도 되는 이유는 erroCode(required) + object(item) 명 + field(itemName) 명 으로 이름을 지었기 때문에 읽어올 수 있다.
         */
        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            errors.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                //global 에러는 ObjectError 사용
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
