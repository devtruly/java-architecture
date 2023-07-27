package com.simpledesign.ndms.common.cdc;

import com.simpledesign.ndms.common.IsDefaultInfoSetPath;
import com.simpledesign.ndms.common.NdmsFirstInitializer;
import com.simpledesign.ndms.common.Utils;
import com.simpledesign.ndms.dto.SetDto;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
/**
 * @author dev-tlury
 * @version 0.0.1
 * CdcEventHandler는 특정 URL로 데이터를 전송하는 프레임워크를 제공하는 추상 클래스입니다.
 * 이 클래스는 이벤트 처리 메커니즘을 사용하여 이 작업을 수행합니다.
 * 각각의 구체적인 서브클래스는 getSearchData(), getEntity(), getRestUrl() 메소드를 구현해야 합니다.
 *
 * @param <E> 처리할 엔티티의 타입 JpaEntity 상속 Entity만 사용 가능
 * @param <S> 엔티티를 가져오는 데 사용될 검색 데이터의 타입
 */
@Component
public abstract class CdcEventHandler<E, S> {
    protected MessageObject messageObject;

    @Autowired
    IsDefaultInfoSetPath defaultInfoSetPath;

    @Value("${ndms.local-government-do.baseurl}")
    protected String baseUrl;

    @Value("${ndms.local-government-do.service-key}")
    protected String serviceKey;

    @Value("${ndms.local-government-do.default-set-path}")
    protected String defaultSetPath;

    /**
     * 메시지 객체를 입력으로 받아 처리를 진행하는 메서드입니다.
     * 검색 데이터를 가져와서 해당 데이터를 기반으로 Entity 리스트를 생성하고, 이를 URL로 전송합니다.
     *
     * @param messageObject 처리할 메시지 객체
     */
    public final void run(MessageObject messageObject) {
        try {
            if (NdmsFirstInitializer.isDefaultInfoSet == 0 && !NdmsFirstInitializer.firstClassList.contains(getClass())) {
                log.info("[{}] 초기 셋팅 진행이 되지 않아 setter 호출을 진행 하지 않았습니다. : {}", getClass().getSimpleName(), messageObject);
                return;
            }

            setMessageObject(messageObject);
            S searchData = getSearchData();
            List<E> entityList = getEntity(searchData);

            if (entityList == null || entityList.isEmpty()) throw new NullPointerException("뷰테이블에 데이터가 존재 하지 않아 setter 페이지를 호출하지 않았습니다.");

            String path = getPath();
            if (!path.startsWith(defaultSetPath) && path.startsWith("/api")) {
                path = path.replaceAll("^/api", defaultSetPath);
            }
            sendDataToUrl(entityList, path);
        } catch (NullPointerException e) {
            log.error("[{}.run] NullPointerException : {}", getClass().getSimpleName(), e.getMessage());
        }
        catch (Exception e) {
            log.error("[{}.run] Exception : {}", getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 이 메서드에서는 검색에 필요한 데이터를 반환해야 합니다.
     *
     * @return 검색 데이터
     */
    protected abstract S getSearchData();

    /**
     * 이 메서드에서는 검색 데이터를 이용해 엔티티 리스트를 가져와야 합니다.
     *
     * @param searchData 검색 데이터
     * @return 엔티티 리스트
     */
    protected abstract List<E> getEntity(S searchData);

    /**
     * 이 메서드에서는 REST 요청을 보낼 Url 정보를 제외한 Path 정보를 반환해야 합니다.
     *
     * @return REST 요청을 보낼 Path ex) /api/getDngr/getDdObsv
     */
    protected abstract String getPath();

    /**
     * Entity 리스트와 URL을 입력으로 받아, 각 Entity를 맵으로 변환한 후 해당 URL에 데이터를 전송하는 메서드입니다.
     * 이때, serviceKey를 함께 보내게 됩니다.
     *
     * @param entityList 전송할 Entity 리스트
     * @param url 데이터를 전송할 URL
     */
    private void sendDataToUrl(List<E> entityList, String url) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> fields = new HashMap<>();

        try {
            for (E entity: entityList) {
                fields = Utils.convertEntityToMap(entity);
                fields.put("serviceKey", serviceKey);
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + url);
                // Map의 각 항목을 쿼리 파라미터로 추가
                fields.forEach(builder::queryParam);
                log.info(builder.toUriString());

                ResponseEntity<SetDto> headerDto = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        null,
                        SetDto.class
                );
                log.info(headerDto.toString());
            }
        }
        catch (HttpClientErrorException e) {
            log.error("[{}.sendDataToUrl] HttpClientErrorException : {}", getClass().getSimpleName(), e.getMessage());
        }
        catch (HttpServerErrorException e) {
            log.error("[{}.sendDataToUrl] HttpServerErrorException : {}", getClass().getSimpleName(), e.getMessage());
        }
        catch (HttpMessageNotReadableException e) {
            log.error("[{}.sendDataToUrl] HttpMessageNotReadableException : {}", getClass().getSimpleName(), e.getMessage());
        }
        catch (RestClientException e) {
            log.error("[{}.sendDataToUrl] RestClientException : {}", getClass().getSimpleName(), e.getMessage());
        }
        catch (Exception e) {
            log.error("[{}.sendDataToUrl] Exception : {}", getClass().getSimpleName(), e.getMessage());
        }
    }
}
