import * as d3 from 'd3';
import * as type from './type';

export const createRetrospectGuide = (
    svg: d3.Selection<SVGSVGElement, unknown, null, unknown>,
    retrospect: string,  // 280~320자의 회고 텍스트
    x: number,           // 사각형의 x 좌표
    y: number,           // 사각형의 y 좌표
    width: number,       // 사각형의 너비
    height: number,      // 사각형의 높이
    settings: type.RadarContribSettings,  // PieLangOnlySettings 인터페이스 사용
    isForcedAnimation: boolean      // 애니메이션 강제 적용 여부
): void => {
    // 사각형 추가
    const rect = svg.append('rect')
        .attr('x', x)
        .attr('y', y)
        .attr('width', width)
        .attr('height', height)
        .attr('fill', settings.radarColor)   // 배경 색상 설정
        .attr('fill-opacity', 0.5)                                   // 불투명도를 50%로 설정
        .attr('stroke', settings.radarColor) // 테두리 색상 설정
        .attr('stroke-width', 2)
        .attr('opacity', 1);  // 애니메이션 없이 즉시 표시

    // 텍스트 줄 수와 줄당 문자 개수 설정
    const maxCharsPerLine = Math.floor(width / 7);  // 한 줄에 들어갈 최대 문자 수 계산
    const lines = wrapText(retrospect, maxCharsPerLine);  // 텍스트를 줄바꿈하여 배열로 저장

    // 텍스트 그룹 추가
    const textGroup = svg.append('g').attr('transform', `translate(${x + 10}, ${y + 30})`);

    // 각 줄에 대해 tspans 추가
    lines.forEach((line, i) => {
        textGroup.append('text')
            .attr('x', 0)
            .attr('y', i * 20) // 줄 간격 20px
            .attr('fill', settings.foregroundColor)
            .style('font-size', '14px')
            .style('font-family', 'Arial')
            .text(line)
            .attr('opacity', 1);  // 애니메이션 없이 즉시 표시
    });
};

// 텍스트를 줄바꿈 처리하는 함수
const wrapText = (text: string, maxCharsPerLine: number): string[] => {
    const words = text.split(' '); // 단어 단위로 나누기
    const lines: string[] = [];
    let currentLine = '';

    words.forEach((word) => {
        if (currentLine.length + word.length + 1 > maxCharsPerLine) {
            lines.push(currentLine); // 현재 줄을 추가
            currentLine = word;      // 새로운 줄 시작
        } else {
            currentLine += (currentLine ? ' ' : '') + word; // 단어 추가
        }
    });

    if (currentLine) {
        lines.push(currentLine); // 마지막 줄 추가
    }

    return lines;
};
