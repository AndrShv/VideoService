package videoservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import videoservice.entity.Video;

import java.util.List;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID>{

    Page<Video> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Video> findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(String title, String category, Pageable pageable);
    Page<Video> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Video> findByCategoryIgnoreCase(String category, Pageable pageable);
    List<Video> findAllByAuthorId(UUID authorId);
}
