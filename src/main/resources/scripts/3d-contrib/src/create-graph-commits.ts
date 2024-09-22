import * as d3 from 'd3';
import * as type from './type';
import * as util from './utils';

export const createBarChartCommits = (
    svg: d3.Selection<SVGSVGElement, unknown, null, unknown>,
    repositoryInfo: type.RepositoryInfo,  // 레포지토리 정보 (커밋, 기여 정보 포함)
    x: number,                           // 차트의 x 좌표
    y: number,                           // 차트의 y 좌표
    width: number,                       // 차트의 너비
    height: number,                      // 차트의 높이
    settings: type.RadarContribSettings, // 설정 (축 색상, 바 색상, 애니메이션 옵션 등)
    isForcedAnimation: boolean           // 애니메이션 강제 적용 여부
): void => {
    // 커밋이 없는 경우 차트를 생성하지 않음
    if (repositoryInfo.commits.length === 0) {
        return;
    }

    // 각 시간대별 커밋 수를 저장하는 배열 (24시간을 기준으로)
    const commitCountsByHour = Array(24).fill(0);

    // 커밋 데이터를 시간대별로 그룹화
    repositoryInfo.commits.forEach((commit) => {
        const commitHour = new Date(commit.date).getUTCHours(); // 커밋이 발생한 시간 (0~23)
        commitCountsByHour[commitHour] += 1;                    // 해당 시간대의 커밋 수 증가
    });

    // 시간대별 최대 커밋 수를 계산 (Y축의 범위를 설정하기 위함)
    const maxCommits = Math.max(...commitCountsByHour);

    // 차트의 여백 설정
    const margin = { top: 40, right: 30, bottom: 60, left: 50 }; // 하단에 여백을 더 추가하여 텍스트 공간 확보
    const chartWidth = width - margin.left - margin.right;  // 차트 너비에서 여백을 제외한 실제 차트 너비
    const chartHeight = height - margin.top - margin.bottom; // 차트 높이에서 여백을 제외한 실제 차트 높이

    // 차트가 그려질 그룹 요소를 추가하고, 차트의 x, y 좌표로 이동
    const group = svg.append('g').attr('transform', `translate(${x}, ${y})`);

    // X축 스케일: 시간대 (0~23시) 범위를 설정
    const xScale = d3
        .scaleBand()
        .domain(commitCountsByHour.map((_, i) => i.toString())) // 0~23 시를 문자열로 변환하여 사용
        .range([0, chartWidth])                                // 차트의 가로 범위
        .padding(0.1);                                         // 막대 간 간격 설정

    // Y축 스케일: 커밋 수 (최대값을 실제 커밋 수의 최대값으로 설정)
    const yScale = d3
        .scaleLinear()
        .domain([0, maxCommits])                               // 최소 0, 최대 커밋 수에 따라 스케일 조정
        .range([chartHeight, 0]);                              // y 값이 클수록 위로 올라가게 스케일링

    // X축 생성 및 축 레이블 색상과 크기를 설정 (축이 차트 아래에 위치하도록 설정)
    group
        .append('g')
        .attr('transform', `translate(0, ${chartHeight})`)    // 차트 아래로 이동
        .call(d3.axisBottom(xScale)
            .tickFormat((d) => d)                             // X축의 각 시간대 (0~23시) 레이블 추가
            .tickSize(2)                                      // 간격 표시를 위한 작은 선 생성
            .tickSizeOuter(0)                                 // 축의 가장자리 선을 없앰
            .tickPadding(5)                                   // 레이블과 축 간격 설정
        )
        .selectAll('text')                                    // 텍스트 스타일 설정
        .attr('fill', settings.foregroundColor)
        .style('font-size', '10px');                          // 텍스트 크기 설정

    // Y축 생성 및 축 레이블 색상과 크기를 설정
    group
        .append('g')
        .call(d3.axisLeft(yScale)
            .ticks(5)                                         // Y축은 5개의 레이블을 생성
            .tickSizeInner(-3)                                // Y축의 작은 선을 왼쪽으로 이동
            .tickSizeOuter(0)                                 // 축의 가장자리 선을 없앰
            .tickPadding(5)                                   // 레이블과 축 간격 설정
        )
        .selectAll('text')                                    // 텍스트 스타일 설정
        .attr('fill', settings.foregroundColor)
        .style('font-size', '10px');                          // 텍스트 크기 설정

    // X축과 Y축 선 스타일 설정 (축 선은 그대로 유지)
    group.selectAll('.domain')
        .attr('stroke', settings.foregroundColor);            // X, Y축 선을 그려줌

    // 막대 차트 생성 및 애니메이션 적용
    const bars = group
        .selectAll('rect')                                      // 각 시간대별로 막대(rect) 생성
        .data(commitCountsByHour)                               // 시간대별 커밋 수 데이터를 바인딩
        .enter()
        .append('rect')
        .attr('x', (d, i) => xScale(i.toString())!)             // 각 막대의 x 좌표 설정 (시간대별 위치)
        .attr('y', chartHeight)                                 // 각 막대가 아래에서 시작하도록 설정
        .attr('width', xScale.bandwidth()-3)                    // 막대 너비 설정 (스케일에 따라), 수정)선을 그리기 위해 조금 줄이기
        .attr('height', 0)                                      // 초기 높이를 0으로 설정
        .attr('fill', settings.radarColor)                      // 막대 색상은 설정 파일에서 가져옴
        .attr('fill-opacity', 0.5)                                   // 불투명도를 50%로 설정
        .attr('stroke', settings.radarColor)                                // 막대의 외곽선 색상 설정 (검정색)
        .attr('stroke-width', 3);                               // 외곽선 두께 설정 (1px)
   
        // 애니메이션 적용
    bars.each(function (d, i) {
        const bar = d3.select(this);
        const heightValue = chartHeight - yScale(d);  // 최종 높이 계산

        // 애니메이션을 추가
        if (isForcedAnimation && d !== 0) {
            bar.append('animate')
                .attr('attributeName', 'height')
                .attr('values', `0;${util.toFixed(heightValue)}`)
                .attr('dur', '3s')
                .attr('fill', 'freeze') // 애니메이션이 끝난 후 마지막 상태에서 멈춤
                .attr('repeatCount', '1');
            
            bar.append('animate')
                .attr('attributeName', 'y')
                .attr('values', `${chartHeight};${yScale(d)}`)
                .attr('dur', '3s')
                .attr('fill', 'freeze') // 애니메이션이 끝난 후 마지막 상태에서 멈춤
                .attr('repeatCount', '1');
        }
    });

    // Commits by Hour 텍스트 추가
    group
        .append('text')
        .attr('x', chartWidth)                                         // 그래프의 가장 왼쪽에 맞춤
        .attr('y', chartHeight + margin.bottom - 10)                    // 하단에 위치하도록 설정
        .attr('text-anchor', 'end')                                     // 텍스트의 끝 위치에 맞춤
        .attr('dominant-baseline', 'middle')                            // 텍스트를 세로 기준선에 맞춤
        .attr('fill', settings.foregroundColor)                         // X축과 동일한 색상 사용
        .style('font-size', '18px')                                     // 텍스트 크기 설정
        .text('Commits by Hour');                                       // 텍스트 내용 설정
};
