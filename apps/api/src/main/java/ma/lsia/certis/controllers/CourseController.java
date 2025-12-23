package ma.lsia.certis.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/courses")
@Tag(name = "Courses", description = "Course management endpoints")
@RequiredArgsConstructor
public class CourseController {

}
