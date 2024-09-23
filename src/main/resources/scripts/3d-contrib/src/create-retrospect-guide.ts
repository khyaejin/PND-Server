import * as d3 from 'd3';
import * as type from './type';

export const createRetrospectGuide = async (
    svg: d3.Selection<SVGSVGElement, unknown, null, unknown>,
    retrospect: string,  // 이미 HTML로 작성된 회고 텍스트
    x: number,           // 사각형의 x 좌표
    y: number,           // 사각형의 y 좌표
    width: number,       // 사각형의 너비
    height: number,      // 사각형의 높이
    settings: type.RadarContribSettings,  // PieLangOnlySettings 인터페이스 사용
    isForcedAnimation: boolean      // 애니메이션 강제 적용 여부
): Promise<void> => {
    // 사각형 추가
    const rect = svg.append('rect')
        .attr('x', x)
        .attr('y', y)
        .attr('width', width)
        .attr('height', height)
        .attr('fill', settings.radarColor)   // 배경 색상 설정
        .attr('fill-opacity', 0.5)           // 불투명도를 50%로 설정
        .attr('stroke', settings.radarColor) // 테두리 색상 설정
        .attr('stroke-width', 2)
        .attr('opacity', 1);                 // 애니메이션 없이 즉시 표시

    // foreignObject를 이용하여 HTML을 SVG 내에 삽입
    svg.append('foreignObject')
        .attr('x', x + 10)
        .attr('y', y + 10)
        .attr('width', width - 20)
        .attr('height', height - 20)
        .html(`<div xmlns="http://www.w3.org/1999/xhtml" style="font-size: 14px; font-family: Arial; color: ${settings.foregroundColor};">${retrospect}</div>`);
};
