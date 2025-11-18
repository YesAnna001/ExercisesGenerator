package app.api.controller;

import app.api.dto.CreateBankRequest;
import app.api.dto.QuestionBankDto;
import app.model.operation.BinaryOperation;
import app.model.QuestionBank;
import app.api.service.QuestionBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Tag(name = "题库管理", description = "题库创建、发布、导入等接口")
@Validated
@RestController
@RequestMapping("/api/banks")
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    public QuestionBankController(QuestionBankService bankService) {
        this.questionBankService = bankService;
    }

    @Operation(
            summary = "创建一个新的题库",
            description = "根据类型和题目数量生成题库，支持 add/sub/mix 三种类型。"
    )
    @PostMapping("/create")
    public ResponseEntity<QuestionBankDto> createBank(@Valid @RequestBody CreateBankRequest req) {
        QuestionBank bank = questionBankService.createByType(req.getType(), req.getCount(), req.getCreator());
        return ResponseEntity.status(HttpStatus.CREATED).body(QuestionBankDto.from(bank));
    }


    /**
     * 上传 CSV 并创建题库（multipart/form-data）
     * POST /api/v1/banks/import
     * form-data: file, creator
     */
    @PostMapping("/import")
    public ResponseEntity<QuestionBankDto> importCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("creator") String creator) throws IOException {

        QuestionBank bank = questionBankService.importFromCsv(file, creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuestionBankDto.from(bank));
    }


    /**
     * 获取题库题目（供学生拉题）
     * GET /api/v1/banks/{id}/questions
     * 可选参数 shuffle, limit
     */
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<BinaryOperation>> getQuestions(
            @PathVariable("id") String id,
            @RequestParam(value = "shuffle", defaultValue = "false") boolean shuffle,
            @RequestParam(value = "limit", required = false) Integer limit) {

        List<BinaryOperation> qs = questionBankService.getQuestions(id);
        if (qs == null) return ResponseEntity.notFound().build();

        if (shuffle) java.util.Collections.shuffle(qs);
        if (limit != null && limit > 0 && limit < qs.size()) {
            qs = qs.subList(0, limit);
        }
        return ResponseEntity.ok(qs);
    }

    /**
     * 发布题库
     * POST /api/v1/banks/{id}/publish
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable("id") String id) {
        questionBankService.publish(id);
        return ResponseEntity.ok().build();
    }
}

