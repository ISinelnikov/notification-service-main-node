package notification.service.backend.controller;

import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.service.SecurityNodeService;
import notification.service.backend.service.UserService;
import notification.service.backend.service.notification.SendingHistoryService;
import notification.service.domain.ApiResponse;
import notification.service.domain.DeleteIdDto;
import notification.service.domain.ServerRegistrationDto;
import notification.service.domain.message.MessageId;
import notification.service.domain.message.SendingInfo;
import notification.service.utils.RequestUtils;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class InternalController {
    private static final Logger logger = LoggerFactory.getLogger(InternalController.class);

    private final UserService userService;
    private final SecurityNodeService nodeService;
    private final SendingHistoryService sendingHistoryService;

    public InternalController(UserService userService, SendingHistoryService sendingHistoryService,
            SecurityNodeService nodeService) {
        this.userService = Objects.requireNonNull(userService,
                "User profile service can't be null.");
        this.sendingHistoryService = Objects.requireNonNull(sendingHistoryService,
                "Sending history repository can't be null.");
        this.nodeService = Objects.requireNonNull(nodeService, "Node service can't be null.");
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> signUpRequest(@RequestBody ServerRegistrationDto signUpDto, HttpServletRequest request) {
        String sourceIp = RequestUtils.getSourceIp(request);
        logger.info("Registry user sign up dto: {}, source: {}", signUpDto, sourceIp);

        String securityToken = RequestUtils.getSecurityToken(request);

        if (StringUtils.hasText(securityToken)) {
            boolean isValidate = nodeService.validateNodeCredentials(securityToken, sourceIp);

            if (isValidate) {
                try {
                    userService.addUserProfileFromRegistrationDto(signUpDto,
                            nodeService.getSecurityEntityIdByTokenValue(securityToken));
                    return ResponseEntity.ok(ApiResponse.getSuccessResponse("Successful update or create user operation."));
                } catch (ModelModificationException ex) {
                    logger.error("Can't save or update user with component server error.");
                    return new ResponseEntity<>(ApiResponse.getFailedResponse("Can't create or update user."),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity<>(ApiResponse.getSuccessResponse("Can't edit or update user. Token not found."),
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * Add new message with user id and message id
     *
     * @param sendingInfo
     * @param request
     *
     * @return
     */
    @PostMapping("/insert-message-id")
    public ResponseEntity<ApiResponse> addMessageId(@RequestBody SendingInfo sendingInfo, HttpServletRequest request) {
        String sourceIp = RequestUtils.getSourceIp(request);
        logger.info("Registry sending info: {}, source: {}", sendingInfo, sourceIp);

        String securityToken = RequestUtils.getSecurityToken(request);

        if (StringUtils.hasText(securityToken)) {
            boolean isValidate = nodeService.validateNodeCredentials(securityToken, sourceIp);
            if (isValidate) {
                sendingHistoryService.insertSendingInfo(sendingInfo);
                return ResponseEntity.ok(ApiResponse.getSuccessResponse("Successful add message info."));
            }
        }

        return new ResponseEntity<>(ApiResponse.getSuccessResponse("Can't add sending info."),
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * Update message id status as delivered
     *
     * @param messageId
     * @param request
     *
     * @return
     */
    @PostMapping("/delivered-message-id")
    public ResponseEntity<ApiResponse> deliveredMessageId(@RequestBody MessageId messageId, HttpServletRequest request) {
        String sourceIp = RequestUtils.getSourceIp(request);
        logger.info("Registry message id: {}, source: {}", messageId, sourceIp);

        String securityToken = RequestUtils.getSecurityToken(request);

        if (StringUtils.hasText(securityToken)) {
            boolean isValidate = nodeService.validateNodeCredentials(securityToken, sourceIp);
            if (isValidate) {
                sendingHistoryService.setDeliveredStatus(messageId);
                return ResponseEntity.ok(ApiResponse.getSuccessResponse("Successful update message status."));
            }
        }

        return new ResponseEntity<>(ApiResponse.getSuccessResponse("Can't update message id."),
                HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/delete-user-id")
    public ResponseEntity<ApiResponse> deleteUserId(@RequestBody DeleteIdDto deleteIdDto, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.getSuccessResponse("Registry delete operation."));
    }
}
