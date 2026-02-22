package com.maplewood.domain;

import java.time.Instant;

import org.hibernate.annotations.Check;
import com.maplewood.persistence.converter.CourseTypeConverter;
import com.maplewood.persistence.converter.InstantStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
@Check(constraints = "credits > 0")
@Check(constraints = "grade_level_min >= 9 AND grade_level_min <= 12")
@Check(constraints = "grade_level_max >= 9 AND grade_level_max <= 12")
@Check(constraints = "grade_level_max >= grade_level_min")
@Check(constraints = "hours_per_week >=2 AND hours_per_week <= 6")
public class Course {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "credits", nullable = false)
    private Double credits;

    @Column(name = "hours_per_week", nullable = false)
    private Integer hoursPerWeek;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_id")
    private Course prerequisite;

    @Convert(converter = CourseTypeConverter.class)
    @Column(name = "course_type", nullable = false, length = 20)
    private CourseType courseType;

    @Column(name = "grade_level_min")
    private Integer gradeLevelMin;

    @Column(name = "grade_level_max")
    private Integer gradeLevelMax;

    @Column(name = "semester_order", nullable = false)
    @Convert(converter = SemesterOrder.SemesterOrderConverter.class)
    private SemesterOrder semesterOrder;

    @Column(name = "created_at")
    @Convert(converter = InstantStringConverter.class)
    private Instant createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Integer getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(Integer hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public Course getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Course prerequisite) {
        this.prerequisite = prerequisite;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public Integer getGradeLevelMin() {
        return gradeLevelMin;
    }

    public void setGradeLevelMin(Integer gradeLevelMin) {
        this.gradeLevelMin = gradeLevelMin;
    }

    public Integer getGradeLevelMax() {
        return gradeLevelMax;
    }

    public void setGradeLevelMax(Integer gradeLevelMax) {
        this.gradeLevelMax = gradeLevelMax;
    }

    public SemesterOrder getSemesterOrder() {
        return semesterOrder;
    }

    public void setSemesterOrder(SemesterOrder semesterOrder) {
        this.semesterOrder = semesterOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
