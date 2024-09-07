export interface CalendarInfo {
    contributionCount: number;
    contributionLevel: number;
    date: Date;
}

export interface LangInfo {
    language: string;
    color: string;
    contributions: number;
}

// 레포지토리 내 기여 정보를 담는 인터페이스 정의
export interface Contribution {
    date: Date;
    count: number;
    level: number;
    contributionCount: number;  // 추가
    contributionLevel: number;  // 추가
}


// 언어 정보를 담는 인터페이스 정의
export interface LanguageInfo {
  language: string;
  color: string;
  contributions: number;
}

// 레포지토리 정보를 담는 인터페이스 정의
export interface RepositoryInfo {
  name: string; // 레포지토리 이름
  forkCount: number; // 포크 수
  stargazerCount: number; // 스타 개수
  primaryLanguage: {
    name: string;
    color: string;
  } | null; // 주요 언어 정보
  contributions: Contribution[]; // 기여 내역
  languages: LanguageInfo[]; // 사용된 언어 정보
  totalContributions: number; // 레포지토리 내 총 기여 수
  totalCommitContributions: number; // 커밋 기여 수
  totalIssueContributions: number; // 이슈 기여 수
  totalPullRequestContributions: number; // 풀 리퀘스트 기여 수
  totalPullRequestReviewContributions: number; // 풀 리퀘스트 리뷰 기여 수
  totalRepositoryContributions: number; // 레포지토리 기여 수
}



export type ContributionLevel =
    | 'NONE'
    | 'FIRST_QUARTILE'
    | 'SECOND_QUARTILE'
    | 'THIRD_QUARTILE'
    | 'FOURTH_QUARTILE';

export interface RadarContribSettings {
    backgroundColor: string;
    foregroundColor: string;
    weakColor: string;
    radarColor: string;

    growingAnimation?: boolean;

    fileName?: string;

    l10n?: {
        commit: string;
        repo: string;
        review: string;
        pullreq: string;
        issue: string;
    };
}

export interface PieLangSettings {
    backgroundColor: string;
    foregroundColor: string;

    growingAnimation?: boolean;

    fileName?: string;
}

export interface BaseSettings extends RadarContribSettings, PieLangSettings {
    backgroundColor: string;
    foregroundColor: string;
    strongColor: string;
    weakColor: string;
    radarColor: string;

    growingAnimation?: boolean;

    fileName?: string;

    l10n?: {
        commit: string;
        repo: string;
        review: string;
        pullreq: string;
        issue: string;
        contrib: string;
    };
}

export interface NormalColorSettings extends BaseSettings {
    type: 'normal';

    contribColors: [string, string, string, string, string];
}

export interface SeasonColorSettings extends BaseSettings {
    type: 'season';

    /** first season (Mar. - Jun.) */
    contribColors1: [string, string, string, string, string];
    /** second season (Jun. - Sep.) */
    contribColors2: [string, string, string, string, string];
    /** third season (Sep. - Dec.) */
    contribColors3: [string, string, string, string, string];
    /** Fourth season (Dec. - Mar.) */
    contribColors4: [string, string, string, string, string];
}

export interface RainbowColorSettings extends BaseSettings {
    type: 'rainbow';

    saturation: string;
    contribLightness: [string, string, string, string, string];
    duration: string; // ex. '10s'
    hueRatio: number; // hue per weeks
}

export interface PanelPattern {
    width: number;
    /** array of (number or hex-string) */
    bitmap: (number | string)[];
}

export interface TopPanelPattern extends PanelPattern {
    backgroundColor: string;
    foregroundColor: string;
}

export interface SidePanelPattern extends PanelPattern {
    /** If omitted, calculate from the topPanel backgroundColor */
    backgroundColor?: string;
    /** If omitted, calculate from the topPanel foregroundColor */
    foregroundColor?: string;
}

export interface ContribPattern {
    top: TopPanelPattern;
    left: SidePanelPattern;
    right: SidePanelPattern;
}

export interface BitmapPatternSettings extends BaseSettings {
    type: 'bitmap';
    growingAnimation?: boolean;

    contribPatterns: [
        ContribPattern,
        ContribPattern,
        ContribPattern,
        ContribPattern,
        ContribPattern
    ];
}

export interface PieLangOnlySettings extends PieLangSettings {
    type: 'pie_lang_only';
}

export interface RadarContribOnlySettings extends RadarContribSettings {
    type: 'radar_contrib_only';
}

export type FullSettings =
    | NormalColorSettings
    | SeasonColorSettings
    | RainbowColorSettings
    | BitmapPatternSettings;

export type Settings =
    | FullSettings
    | PieLangOnlySettings
    | RadarContribOnlySettings;

export type SettingFile = Settings | Settings[];
