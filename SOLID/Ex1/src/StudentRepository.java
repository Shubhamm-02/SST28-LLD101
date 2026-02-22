public interface StudentRepository {
    void save(StudentRecord student);

    int count();
}
