var gulp = require('gulp');
var ts = require("gulp-typescript");
var tsProject = ts.createProject("tsconfig.json");
var watch = require('gulp-watch');

gulp.task('default', defaultTask);

function defaultTask(done) {
    gulp.start('compileTs');
}

gulp.task('compileTs', compileTsTask);

function compileTsTask(done) {
    return tsProject.src()
        .pipe(tsProject())
        .js.pipe(gulp.dest("dist"));
}

gulp.task('watch', ['compileTs'], function() {
    gulp.watch('src/*.ts', ['compileTs']);
});