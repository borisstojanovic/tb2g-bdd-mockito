package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    private static final String REDIRECT_OWNERS_1 = "redirect:/owners/1";
    private static final String OWNERS_FIND_OWNERS = "owners/findOwners";
    private static final String OWNERS_OWNERS_LIST = "owners/ownersList";

    @Mock
    BindingResult bindingResult;

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController ownerController;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willAnswer(invocation -> {
                    String name = invocation.getArgument(0);
                    List<Owner> ownerList = new ArrayList<>();

                    switch (name) {
                        case "%Buck%":
                            ownerList.add(new Owner(1L, "Joe", "Buck"));
                            return ownerList;
                        case "%DontFindMe%":
                            return ownerList;
                        case "%FindMe%":
                            ownerList.add(new Owner(1L, "Joe", "Buck1"));
                            ownerList.add(new Owner(2L, "Joe", "Buck2"));
                            return ownerList;
                    }

                    throw new RuntimeException("Invalid Argument");
                });
    }

    @Test
    void testProcessFindFormWildcardsStringAnnotation() {
        //given
        Owner owner = new Owner(1L, "Joe", "Buck");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat(REDIRECT_OWNERS_1).isEqualToIgnoringCase(viewName);
    }

    @Test
    void testProcessFindFormWildcardsStringNotFound() {
        //given
        Owner owner = new Owner(1L, "Joe", "DontFindMe");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%DontFindMe%")
                .isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat(OWNERS_FIND_OWNERS)
                .isEqualToIgnoringCase(viewName);
    }

    @Test
    void testProcessFindFormWildcardsStringFound() {
        //given
        Owner owner = new Owner(1L, "Joe", "FindMe");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, Mockito.mock(Model.class));

        //then
        assertThat("%FindMe%")
                .isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat(OWNERS_OWNERS_LIST)
                .isEqualToIgnoringCase(viewName);
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