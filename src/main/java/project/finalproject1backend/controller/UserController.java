package project.finalproject1backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.finalproject1backend.dto.ErrorDTO;
import project.finalproject1backend.dto.ModifyRequestDTO;
import project.finalproject1backend.dto.PrincipalDTO;
import project.finalproject1backend.dto.ResponseDTO;
import project.finalproject1backend.dto.user.*;
import project.finalproject1backend.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/")
@RestController
public class UserController {

    private final UserService userService;
    @Tag(name = "API 로그인/회원가입", description = "로그인/회원가입 api 입니다.")
    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping(value = "/signup",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@RequestPart(value = "requestDTO") @Valid UserSignUpRequestDTO requestDTO,
                                              BindingResult bindingResult,@RequestPart(required = false) List<MultipartFile> businessLicense) {
        return userService.signUp(requestDTO,businessLicense);
    }


    @Tag(name = "API 로그인/회원가입", description = "로그인/회원가입 api 입니다.")
    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = UserLoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequestDTO requestDTO,
                                    BindingResult bindingResult) {
        return userService.login(requestDTO);
    }


    @Tag(name = "API 로그인/회원가입", description = "로그인/회원가입 api 입니다.")
    @Operation(summary = "회원탈퇴 메서드", description = "회원탈퇴 메서드입니다.",security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/account/delete")
    public ResponseEntity<?> delete(@Parameter(hidden = true)@AuthenticationPrincipal PrincipalDTO principal) {
        return userService.delete(principal);
    }


    @Tag(name = "API 마이페이지", description = "마이페이지 api 입니다.")
    @Operation(summary = "마이 페이지(account 정보 조회)", description = "마이 페이지(account 정보 조회) 메서드입니다.",security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = UserInfoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @GetMapping("/account")
    public ResponseEntity<?> getUser(@Parameter(hidden = true)@AuthenticationPrincipal PrincipalDTO principal) {
        return new ResponseEntity<>(new UserInfoResponseDTO(principal),HttpStatus.OK);
    }


    @Tag(name = "API 마이페이지", description = "마이페이지 api 입니다.")
    @Operation(summary = "마이 페이지(account 정보수정)", description = "마이 페이지(account 정보수정) 메서드입니다.'password','phoneNumber','managerName','email','companyName' 수정 가능합니다.",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ModifyRequestDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/account/modify")
    public ResponseEntity<?> modify(@Parameter(hidden = true)@AuthenticationPrincipal PrincipalDTO principal, @RequestBody ModifyRequestDTO modifyRequestDTO) {
        return userService.modify(principal,modifyRequestDTO);
    }


    @Tag(name = "API 마이페이지", description = "마이페이지 api 입니다.")
    @Operation(summary = "마이 페이지(account 정보수정) ⇒ 사업자등록증수정", description = "마이 페이지(account 정보수정)⇒ 사업자등록증수정 메서드입니다.",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping(value = "/account/modifyLicense",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyLicense(@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDTO principal, @RequestPart(value = "requestDTO") UserModifyLicenseRequestDTO modifyRequestDTO,@RequestPart(required = false) List<MultipartFile> businessLicense) {
        return userService.modifyLicense(principal,modifyRequestDTO,businessLicense);
    }


    @Tag(name = "API 관리자페이지", description = "관리자페이지 api 입니다.")
    @Operation(summary = "관리자 페이지(고객관리) 전체조회", description = "관리자 페이지(고객관리) 전체조회 메서드입니다.select : “업체명”,”ROLE_REFUSE “ROLE_USER”,”ROLE_STANDBY, “담당자명”",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = UsersInfoDTO.class))),
    })
    @GetMapping("/account/admin/users")
//    public ResponseEntity<?> getUsers(@Parameter(hidden = true) @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam String select,@RequestParam String value) {
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String select,@RequestParam(required = false) String value) {
//        return userService.getUsers(pageable,select,value);
        return userService.getUsers(select,value);
    }


    @Tag(name = "API 관리자페이지", description = "관리자페이지 api 입니다.")
    @Operation(summary = "관리자 페이지(고객관리) 선택조회", description = "관리자 페이지(고객관리) 선택조회 메서드입니다.",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = UserInfoResponseDTO.class))),
    })
    @GetMapping("/account/admin/usersInfo/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        return userService.getUserInfo(userId);
    }


    @Tag(name = "API 관리자페이지", description = "관리자페이지 api 입니다.")
    @Operation(summary = "관리자 페이지(고객관리)ROLE_USER로 변경", description = "관리자 페이지(고객관리)ROLE_USER로 변경 메서드입니다.",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
    })
    @PostMapping("/account/admin/users/roleUser/{userId}")
    public ResponseEntity<?> setRoleUser(@PathVariable String userId) {
        return userService.roleUser(userId);
    }


    @Tag(name = "API 관리자페이지", description = "관리자페이지 api 입니다.")
    @Operation(summary = "관리자 페이지(고객관리)ROLE_STANDBY로 변경", description = "관리자 페이지(고객관리)ROLE_STANDBY로 변경 메서드입니다.",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
    })
    @PostMapping("/account/admin/users/roleStandby/{userId}")
    public ResponseEntity<?> setRoleStandby(@PathVariable String userId) {
        return userService.roleStandby(userId);
    }

    @Tag(name = "API 관리자페이지", description = "관리자페이지 api 입니다.")
    @Operation(summary = "관리자 페이지(고객관리)ROLE_REFUSE로 변경", description = "관리자 페이지(고객관리)ROLE_REFUSE로 변경 메서드입니다.",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
    })
    @PostMapping("/account/admin/users/roleRefuse/{userId}")
    public ResponseEntity<?> setRoleRefuse(@PathVariable String userId) {
        return userService.roleRefuse(userId);
    }

    @Tag(name = "API 관리자페이지", description = "관리자페이지 api 입니다.")
    @Operation(summary = "관리자 페이지(고객관리)회원 수 조회", description = "관리자 페이지(고객관리)회원 수 조회 메서드입니다.",
            security ={ @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = UserCountResponseDTO.class))),
    })
    @GetMapping("/account/admin/users/getUserCount/{content}")
    public ResponseEntity<?> getUserCount() {
        return userService.getUserCount();
    }
}