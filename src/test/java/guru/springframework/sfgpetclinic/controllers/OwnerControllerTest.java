package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock
    BindingResult bindingResult;

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController ownerController;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Test
    void testProcessFindFormWildcardsString() {
        //given
        Owner owner = new Owner(1L, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        given(ownerService.findAllByLastNameLike(argumentCaptor.capture()))
                .willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%buck%").isEqualToIgnoringCase(argumentCaptor.getValue());
    }

    @Test
    void testProcessFindFormWildcardsStringAnnotation() {
        //given
        Owner owner = new Owner(1L, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willReturn(ownerList);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
    }

    @Test
    void testProcessCreationFormWithErrors() {
        //given
        Owner owner = new Owner(1L, "Boris", "Stojanovic");
        given(bindingResult.hasErrors())
                .willReturn(true);

        //when
        String viewName = ownerController.processCreationForm(owner, bindingResult);

        //then
        assertThat(viewName).isEqualToIgnoringCase(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
    }

    @Test
    void testProcessCreationFormNoErrors() {
        //given
        Owner owner = new Owner(5L, "Boris", "Stojanovic");
        given(bindingResult.hasErrors())
                .willReturn(false);
        given(ownerService.save(any(Owner.class)))
                .willReturn(owner);

        //when
        String viewName = ownerController.processCreationForm(owner, bindingResult);

        //then
        assertThat(viewName).isEqualToIgnoringCase(REDIRECT_OWNERS_5);
    }
}