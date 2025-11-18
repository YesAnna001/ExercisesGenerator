package app.api.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 题库映射
 * 用来接受前端传来的参数
 */
public class CreateBankRequest {

    @NotBlank(message = "type 不能为空")
    private String type;

    @Min(value = 1, message = "count 必须大于 0")
    private int count;

    @NotBlank(message = "creator 不能为空")
    private String creator;

    // getter/setter
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
}

