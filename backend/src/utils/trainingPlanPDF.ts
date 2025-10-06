export const generateTrainingPlanHTML = (trainingPlan: any): string => {
  const goalTranslations: Record<string, string> = {
    weight_loss: 'Redukcja',
    muscle_gain: 'Masa mięśniowa',
    strength: 'Siła',
    endurance: 'Wytrzymałość',
    flexibility: 'Elastyczność',
  };

  const levelTranslations: Record<string, string> = {
    none: 'Nie wybrano',
    beginner: 'Początkujący',
    intermediate: 'Średniozaawansowany',
    advanced: 'Zaawansowany',
    easy: 'Początkujący',
    medium: 'Średniozaawansowany',
    hard: 'Zaawansowany',
  };

  const getLevelColorClass = (level: string): string => {
    const normalizedLevel = level ? level.toString().toLowerCase().trim() : 'none';
    const colorClasses: Record<string, string> = {
      none: 'level-unknown',
      beginner: 'level-beginner',
      intermediate: 'level-intermediate',
      advanced: 'level-advanced',
      easy: 'level-beginner',
      medium: 'level-intermediate',
      hard: 'level-advanced',
      początkujący: 'level-beginner',
      średniozaawansowany: 'level-intermediate',
      zaawansowany: 'level-advanced',
      poczatkujacy: 'level-beginner',
      sredniozaawansowany: 'level-intermediate',
    };
    return colorClasses[normalizedLevel] || 'level-unknown';
  };

  const formatDuration = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;

    if (hours > 0) {
      return `${hours}h ${mins}min`;
    }
    return `${mins}min`;
  };

  const formatTotalDuration = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;

    if (hours > 0) {
      return `${hours}h ${mins}min`;
    }
    return `${mins}min`;
  };

  const formatSetDuration = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    if (minutes > 0) {
      if (remainingSeconds > 0) {
        return `${minutes}min ${remainingSeconds}s`;
      }
      return `${minutes}min`;
    }
    return `${seconds}s`;
  };

  return `
    <!DOCTYPE html>
    <html lang="pl">
    <head>
      <meta charset="UTF-8">
      <style>
        * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
        }

        html {
          margin: 0;
          padding: 0;
        }

        body {
          font-family: 'Arial', sans-serif;
          background: linear-gradient(to bottom, #f5f5f5 0%, #e8f5e9 100%);
          position: relative;
          margin: 0;
          padding: 0;
        }

        body::before {
          content: '';
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background-image: radial-gradient(circle, #c5c0c0ff 1.5px, transparent 1.5px);
          background-size: 8mm 8mm;
          background-position: 4mm 4mm;
          z-index: 0;
          pointer-events: none;
        }

        .page-background {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          z-index: 0;
          pointer-events: none;
        }

        .green-circle-top {
          position: absolute;
          top: -100mm;
          right: -80mm;
          width: 250mm;
          height: 250mm;
          background: radial-gradient(circle, rgba(139, 195, 74, 0.15) 0%, transparent 70%);
          border-radius: 50%;
          filter: blur(60px);
        }

        .green-circle-bottom {
          position: absolute;
          bottom: -120mm;
          right: -100mm;
          width: 280mm;
          height: 280mm;
          background: radial-gradient(circle, rgba(139, 195, 74, 0.12) 0%, transparent 70%);
          border-radius: 50%;
          filter: blur(80px);
        }

        .green-stripe {
          position: fixed;
          top: 4px;
          bottom: 4px;
          right: 10px;
          width: 3mm;
          height: auto; 
          background: linear-gradient(180deg, #c8e4b1ff 0%, #5b9020ff 100%);
          z-index: 1;
          clip-path: polygon(0 0, 100% 15mm, 100% 100%, 0 100%);
        }

        .logo {
          position: fixed;
          top: 4mm;
          right: 6mm;
          z-index: 2;
          width: 80px;
          height: 80px;
        }

        .logo img {
          width: 100%;
          height: 100%;
          object-fit: contain;
          image-rendering: -webkit-optimize-contrast;
          image-rendering: high-quality;
        }

        .content {
          position: relative;
          z-index: 1;
          padding: 15mm 10mm 15mm 10mm;
          max-width: 100%;
        }

        .header {
          margin-bottom: 12mm;
          padding-right: 90px;
        }

        .plan-title {
          font-size: 28px;
          font-weight: bold;
          color: #2c3e50;
          margin-bottom: 8px;
        }

        .plan-meta {
          display: flex;
          gap: 15px;
          flex-wrap: wrap;
          margin-bottom: 10px;
        }

        .meta-item {
          background: white;
          padding: 6px 12px;
          border-radius: 6px;
          font-size: 12px;
          color: #666;
          box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .level-beginner {
          background: #556c1d !important;
          color: white !important;
          font-weight: bold;
        }

        .level-intermediate {
          background: #efb100 !important;
          color: white !important;
          font-weight: bold;
        }

        .level-advanced {
          background: #a71d0b !important;
          color: white !important;
          font-weight: bold;
        }

        .level-unknown {
          background: #9e9e9e !important;
          color: white !important;
          font-weight: bold;
        }

        .meta-label {
          font-weight: bold;
          color: #333;
        }

        .level-beginner .meta-label,
        .level-intermediate .meta-label,
        .level-advanced .meta-label,
        .level-unknown .meta-label {
          color: white !important;
        }

        .plan-description {
          font-size: 13px;
          color: #555;
          line-height: 1.6;
          margin-top: 10px;
          background: rgba(255,255,255,0.7);
          padding: 12px;
          border-radius: 8px;
        }

        .day {
          margin-bottom: 10mm;
        }

        .day-header {
          background: linear-gradient(135deg, #8bc34a 0%, #689f38 100%);
          color: white;
          padding: 10px 15px;
          border-radius: 8px;
          margin-bottom: 8px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          page-break-after: avoid;
        }

        .day-title {
          font-size: 16px;
          font-weight: bold;
        }

        .day-duration {
          font-size: 12px;
          background: rgba(255,255,255,0.2);
          padding: 4px 10px;
          border-radius: 12px;
        }

        .rest-day {
          background: linear-gradient(135deg, #90a4ae 0%, #607d8b 100%);
        }

        .exercise {
          background: white;
          padding: 12px;
          margin-bottom: 8px;
          border-radius: 6px;
          box-shadow: 0 2px 4px rgba(0,0,0,0.08);
          page-break-inside: avoid;
          break-inside: avoid;
        }

        .exercise-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
        }

        .exercise-name {
          font-size: 14px;
          font-weight: bold;
          color: #2c3e50;
        }

        .exercise-muscle {
          font-size: 11px;
          color: #666;
          background: #f0f0f0;
          padding: 3px 8px;
          border-radius: 4px;
        }

        .sets-container {
          display: flex;
          gap: 6px;
          flex-wrap: wrap;
        }

        .set {
          background: #f5f5f5;
          padding: 6px 10px;
          border-radius: 4px;
          font-size: 12px;
          color: #555;
        }

        .rest-info {
          font-size: 11px;
          color: #888;
          margin-top: 6px;
          font-style: italic;
        }

        .summary {
          margin-top: 12mm;
          background: white;
          padding: 15px;
          border-radius: 8px;
          box-shadow: 0 2px 8px rgba(0,0,0,0.1);
          page-break-inside: avoid;
        }

        .summary-title {
          font-size: 16px;
          font-weight: bold;
          color: #2c3e50;
          margin-bottom: 10px;
        }

        .summary-stats {
          display: flex;
          gap: 15px;
          flex-wrap: wrap;
        }

        .stat {
          flex: 1;
          min-width: 120px;
        }

        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #8bc34a;
        }

        .stat-label {
          font-size: 12px;
          color: #666;
        }

        .hashtags {
          margin-top: 10mm;
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
        }

        .hashtag {
          background: #e8f5e9;
          color: #689f38;
          padding: 5px 12px;
          border-radius: 15px;
          font-size: 11px;
          font-weight: 500;
        }

      @media print {

        @page :first {
          margin-top: 0mm;
        }
        
          body::before {
            -webkit-print-color-adjust: exact;
            print-color-adjust: exact;
          }
            
          .body::before,
          .page-background,
          .green-stripe,
          .logo {
            -webkit-print-color-adjust: exact;
            print-color-adjust: exact;
          }
          
          .day {
            page-break-inside: auto;
          }
          
          .exercise {
            page-break-inside: avoid;
            break-inside: avoid;
          }
          
          .day-header {
            page-break-after: avoid;
          }
          
          .summary {
            page-break-inside: avoid;
          }
        }
      </style>
    </head>
    <body>

      <div class="page-background">
        <!-- Zielone koła w tle -->
        <div class="green-circle-top"></div>
        <div class="green-circle-bottom"></div>
      </div>

      <div class="green-stripe"></div>

      <div class="logo">
        <img alt="" src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzA3IiBoZWlnaHQ9IjMwNyIgdmlld0JveD0iMCAwIDMwNyAzMDciIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxwYXRoIGQ9Ik0xMzEuMjc3IDQ5LjQ0MzVDMTM1LjMyOSA1MS4zNDc4IDEzMy45NzggNTguNzM4MyAxMzIuNzk2IDYyLjE5NTZDMTIzLjk1OSA4Ny45MjY0IDEwNS40NCAxNDEuOTM4IDEwMi4wNjMgMTUyLjE0Qzk4LjY4NTkgMTYyLjM0MiA5MC45NzQ0IDE2MC40NzEgODcuNTQwOSAxNTguMjYxQzgxLjU3NDQgMTU2LjI3NyA2OC41MjcgMTUxLjc2NiA2NC4wNjkgMTQ5LjU5QzU5LjYxMSAxNDcuNDEzIDYwLjk3MzEgMTQxLjg4MiA2Mi4yMTE1IDEzOS4zODhDNzIuMDYxOCAxMTAuMDMgOTIuMjM1NCA0OS44NTE2IDk0LjEyNjYgNDQuMDAyNkM5Ni4wMTc5IDM4LjE1MzcgMTAxLjU1NyAzOC42MTg1IDEwNC4wOSAzOS41ODE5QzExMS40NjMgNDIuMDc1NyAxMjcuMjI0IDQ3LjUzOTIgMTMxLjI3NyA0OS40NDM1WiIgZmlsbD0iYmxhY2siLz4KPHBhdGggZD0iTTU2LjgyMDUgMTMwTDgxIDYwLjAyNTRDNzUuODcxIDU4LjE4NCA2NC44MzUzIDU0LjIzMzIgNjEuNzI0IDUzLjE2MTlDNTkuMDE4NiA1Mi4yMzAzIDU1Ljg5IDU1LjUwNTQgNTQuOTYwNSA1OC4xODRDNDkuMjY4IDc0LjU4OTUgMzcuNDQzMSAxMDguNzQgMzUuNjg0NiAxMTQuMDk3QzMzLjkyNjEgMTE5LjQ1NCAzNS45NjY1IDEyMi4xMzIgMzcuMjA2NCAxMjIuODAyTDU2LjgyMDUgMTMwWiIgZmlsbD0iYmxhY2siLz4KPHBhdGggZD0iTTE5Ny43MjMgMTk1LjY0M0MxOTMuNjcxIDE5My43NTQgMTk1LjAyMiAxODYuNDI1IDE5Ni4yMDQgMTgyLjk5NkMyMDUuMDQxIDE1Ny40NzggMjIzLjU2IDEwMy45MTIgMjI2LjkzNyA5My43OTQ5QzIzMC4zMTQgODMuNjc3NiAyMzguMDI2IDg1LjUzMjUgMjQxLjQ1OSA4Ny43MjQ1QzI0Ny40MjYgODkuNjkxOCAyNjAuNDczIDk0LjE2NTkgMjY0LjkzMSA5Ni4zMjQzQzI2OS4zODkgOTguNDgyNiAyNjguMDI3IDEwMy45NjggMjY2Ljc4OSAxMDYuNDQyQzI1Ni45MzggMTM1LjU1NyAyMzYuNzY1IDE5NS4yMzggMjM0Ljg3MyAyMDEuMDM5QzIzMi45ODIgMjA2LjgzOSAyMjcuNDQzIDIwNi4zNzggMjI0LjkxIDIwNS40MjNDMjE3LjUzNyAyMDIuOTUgMjAxLjc3NiAxOTcuNTMxIDE5Ny43MjMgMTk1LjY0M1oiIGZpbGw9ImJsYWNrIi8+CjxwYXRoIGQ9Ik0yNzAuMTc5IDEyNEwyNDYgMTkzLjk3NUMyNTEuMTI5IDE5NS44MTYgMjYyLjE2NSAxOTkuNzY3IDI2NS4yNzYgMjAwLjgzOEMyNjcuOTgxIDIwMS43NyAyNzEuMTEgMTk4LjQ5NSAyNzIuMDM5IDE5NS44MTZDMjc3LjczMiAxNzkuNDEgMjg5LjU1NyAxNDUuMjYgMjkxLjMxNSAxMzkuOTAzQzI5My4wNzQgMTM0LjU0NiAyOTEuMDM0IDEzMS44NjggMjg5Ljc5NCAxMzEuMTk4TDI3MC4xNzkgMTI0WiIgZmlsbD0iYmxhY2siLz4KPHBhdGggZD0iTTEyMCAxMjUuMjA1TDEzMC41MjggOTRMMjEwIDEyMC45NjVMMTk5LjMwMiAxNTJMMTIwIDEyNS4yMDVaIiBmaWxsPSJibGFjayIvPgo8cGF0aCBkPSJNNTIuNDE5MiAxODIuMTgyTDYxLjU2MTUgMTc5LjY2M0M1Ny42Njc0IDE2Ny40IDQ5LjIwMiAxNjMuNTM5IDM5Ljg5MDQgMTY0LjA0M0MyNy43IDE2NC43MDMgMTkuNjEyNCAxNzEuMDkyIDE2LjAxNzggMTgyLjE4MkMxMi4zMTIyIDE5My42MTUgMTkuNDA1MiAyMDYuODY5IDI2LjM0NjIgMjEwLjkwMUMzMy44NjI4IDIxNS4yNjggNDYuMTU1NiAyMTQuOTA4IDQ5LjU0MSAyMTMuMjU0QzU0LjExNzcgMjExLjAxOSA1Ni43NzU4IDIwNy44MjUgNTkuMTkxNSAyMDQuNTIxVjIxMy4yNTRINjcuMzE4M1YxODYuODg1SDM1LjgyNzFWMTkzLjA5OUg1Ni40ODI2QzU0LjkxOTIgMTk5LjEyNyA1My4yNjU0IDIwMy4wMDkgNDcuMzQgMjA0LjUyMUMzNy4yNTc3IDIwNi4zMTEgMzMuNjE0NyAyMDQuNjE1IDI5LjU2MjcgMTk4LjMwNkMyNS42OTkzIDE5MC40ODggMjYuMTc2MiAxODQuMTk4IDMwLjQwODkgMTc5LjE1OUMzNC41MDA1IDE3NC4yODggMzkuMzgyMiAxNzMuMDQxIDQ0LjYzMDcgMTczLjk1MkM0OS44NzkzIDE3NC44NjQgNTEuMDQwNSAxNzguODU5IDUyLjQxOTIgMTgyLjE4MloiIGZpbGw9ImJsYWNrIi8+CjxwYXRoIGQ9Ik03My40MTMxIDE3OS42NjNDNzMuNDEzMSAxNzkuNjYzIDczLjQxMzEgMTk2LjEyMSA3My40MTMxIDIwNi4zNjhDNzMuNDEzMSAyMTAuOTEzIDc3LjM5MTcgMjEzLjA3NiA4MS43MDkyIDIxNC41OThDODUuMDczNiAyMTUuNzg0IDg3LjU3NzYgMjE2LjAxIDkwLjg1MTggMjE0LjU5OEM5NC4xNTU2IDIxMy4xNzMgOTYuMDc0NyAyMTAuMzkxIDk3LjExNjIgMjA3Ljg4Qzk3LjExNjIgMjA3Ljg4IDk3LjExNjIgMjExLjkwOSA5Ny4xMTYyIDIxNC41OThDOTcuMTE2MiAyMTcuOTM5IDk0Ljk0NzkgMjE5LjQyNCA5MS44NjU2IDIyMC43NjlDODguMTc0MSAyMjIuMzgxIDg0LjM0OTIgMjIxLjc0NSA4MC41MjQgMjIwLjQ3NkM3Ny4zNTg2IDIxOS40MjcgNzUuNzgxNCAyMTguNDE4IDczLjQxMzEgMjE2LjYxM0w2OS44NTc2IDIyNC4wMDRDNzQuNzcwNyAyMjYuOTkyIDc2LjgzMjEgMjI3LjY4NCA4MS43MDkyIDIyOC43MDZDODcuNTk1OSAyMjkuOTQxIDkzLjczMDcgMjMwLjI5OCA5Ny4xMTYyIDIyOC43MDZDMTAwLjg0MiAyMjYuOTU1IDEwNi45MzYgMjIyLjY3OCAxMDYuOTM2IDIxNi42MTNDMTA2LjkzNiAyMDAuODI0IDEwNi45MzYgMTc5LjY2MyAxMDYuOTM2IDE3OS42NjNIOTcuMTE2MkM5Ny4xMTYyIDE3OS42NjMgOTcuMTE2MiAxOTMuMDk5IDk3LjExNjIgMjAyLjUwNUM5Ny4xMTYyIDIwNC43MzMgOTIuODgzNSAyMDYuMzY4IDkyLjg4MzUgMjA2LjM2OEM5Mi44ODM1IDIwNi4zNjggODguNzUzNCAyMDcuNTU0IDg2LjYxOTEgMjA2LjM2OEM4NC45MzQ0IDIwNS40MzIgODMuNTcxNiAyMDQuNDIxIDgzLjU3MTYgMjAyLjUwNUM4My41NzE2IDE5My4wOTkgODMuNTcxNiAxNzkuNjYzIDgzLjU3MTYgMTc5LjY2M0g3My40MTMxWiIgZmlsbD0iYmxhY2siLz4KPHBhdGggZD0iTTExMi4zNTQgMTc5LjY2M1YyMTQuNTk4SDEyMy4zNTlDMTIzLjM1OSAyMTQuNTk4IDEyMy4zNTkgMTk1LjQwOCAxMjMuMzU5IDE5MS40MkMxMjMuMzU5IDE4Ny40MzIgMTI2LjgzNiAxODYuNjM2IDEyOS45NjIgMTg2Ljg4NUMxMzIuNzcyIDE4Ny4xMDkgMTM1LjU0OSAxODcuOTM2IDEzNS41NDkgMTkxLjQyQzEzNS41NDkgMTk0LjkwNCAxMzUuNTQ5IDIxNC41OTggMTM1LjU0OSAyMTQuNTk4SDE0NS44NzdDMTQ1Ljg3NyAyMTQuNTk4IDE0NS44NzcgMTk0LjU2OCAxNDUuODc3IDE5MS40MkMxNDUuODc3IDE4OC4yNzIgMTQ5LjIyOSAxODYuODA2IDE1Mi4zMSAxODYuODg1QzE1NS4yODUgMTg2Ljk2MSAxNTguNDA1IDE4OC40NCAxNTguNDA1IDE5MS40MkMxNTguNDA1IDE5NC40IDE1OC40MDUgMjE0LjU5OCAxNTguNDA1IDIxNC41OThIMTY4LjM5NUMxNjguMzk1IDIxNC41OTggMTY4LjM5NSAxOTIuMjE3IDE2OC4zOTUgMTg2Ljg4NUMxNjguMzk1IDE4MS41NTMgMTY2LjQzNCAxNzkuODk5IDE2MS40NTMgMTc4LjQ4N0MxNjEuNDUzIDE3OC40ODcgMTU3LjQxNCAxNzcuMDUzIDE1My4zMjYgMTc4LjQ4N0MxNDkuMjM5IDE3OS45MjIgMTQ3Ljc0NSAxODEuNjcgMTQ1Ljg3NyAxODUuNzA5QzE0Mi44NzEgMTgxLjM2NyAxNDEuMTA5IDE3OS4zMjQgMTM3LjQxMSAxNzguNDg3QzEzMy43MTQgMTc3LjY1MSAxMzIuNzMgMTc3LjYgMTI5Ljk2MiAxNzguNDg3QzEyNi4wNDYgMTc5Ljc0MyAxMjQuMjkgMTgxLjQ2OSAxMjIuMzQzIDE4NS43MDlWMTc5LjY2M0gxMTIuMzU0WiIgZmlsbD0iYmxhY2siLz4KPHBhdGggZD0iTTE2OC4zOTUgMjE5LjEzM0gxMzUuNTQ5VjI2Ni40OTdIMTY5LjI0MVYyNjEuMjlIMTQxLjMwNVYyNDUuMTY2SDE2Ni4wMjRWMjM5LjQ1NkgxNDEuMzA1VjIyNC4xNzFIMTY4LjM5NVYyMTkuMTMzWiIgZmlsbD0iYmxhY2siLz4KPHBhdGggZmlsbC1ydWxlPSJldmVub2RkIiBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGQ9Ik0xNzIuMjg5IDI0MS42MzlDMTcyLjI4OSAyNDEuNjM5IDE3Ni4xNDYgMjQ1LjE2NiAxNzcuMzY4IDI0My40ODdDMTc4LjU5IDI0MS44MDcgMTgwLjU4NSAyMzguNDQ4IDE4My4yOTQgMjM3LjIyOUMxODYuMDAzIDIzNi4wMTEgMTg4LjM1MiAyMzUuNTUxIDE5Mi4wOTggMjM2LjI2NEMxOTUuODQzIDIzNi45NzggMTk3LjM4OSAyMzcuOCAxOTguODcgMjQwLjQ2M0MyMDAuMzUxIDI0My4xMjcgMTk4Ljg3IDI0NS4xNjYgMTk4Ljg3IDI0NS4xNjZDMTk4Ljg3IDI0NS4xNjYgMTg4LjUyOCAyNDUuNjU0IDE4MS45MzkgMjQ3LjE4MkMxNzUuMzUxIDI0OC43MDkgMTczLjc0IDI1MC4xOTkgMTcyLjI4OSAyNTQuNzRDMTcxLjY2OCAyNTYuNjgyIDE3MS41MjMgMjU3Ljk1MyAxNzEuOTQ4IDI1OS45NDZDMTcyLjg0OSAyNjQuMTY4IDE3NS45OTYgMjY2LjAwNCAxODAuMDc1IDI2Ny41MDRDMTgzLjczMyAyNjguODUgMTg2LjAzNyAyNjguNjcgMTkwLjA2NiAyNjcuNTA0QzE5NC4wOTUgMjY2LjMzOSAxOTguODcgMjYxLjI5IDE5OC44NyAyNjEuMjlDMTk4Ljg3IDI2MS4yOSAxOTkuMDA0IDI2NS42MTcgMjAwLjkgMjY3LjEyNkMyMDIuOTY2IDI2OC43NyAyMDYuMTQ4IDI2OC40ODYgMjA3LjY3MiAyNjcuNTA0QzIwOS4xOTYgMjY2LjUyMyAyMTAuMDQyIDI2Ni4xMTggMjExLjU2OCAyNjQuODE3TDIwOC44NTkgMjYxLjI5QzIwNi40NSAyNjQuNjQyIDIwMy45NDkgMjYxLjI5IDIwMy45NDkgMjYxLjI5QzIwMy45NDkgMjYxLjI5IDIwMy45NDkgMjQxLjYzOSAyMDMuOTQ5IDIzOC40NDhDMjAzLjk0OSAyMzUuMjU3IDE5OS4wOTcgMjMyLjE1OSAxOTMuMjgzIDIzMS4wNThDMTg3LjQ2OCAyMjkuOTU2IDE4My4yOTQgMjMxLjA1OCAxODMuMjk0IDIzMS4wNThDMTc2Ljk3NiAyMzMuOTcyIDE3NC41OCAyMzYuMjk4IDE3Mi4yODkgMjQxLjYzOVpNMTgwLjU4NSAyNTIuMzg4QzE4Mi42ODIgMjUwLjc5NSAxOTguODcgMjUwLjAzNyAxOTguODcgMjUwLjAzN0MxOTkuMzQgMjU2LjcwNCAxOTcuOTM3IDI1OS42MDIgMTkwLjkxMyAyNjIuNDY2QzE4Ni4xNzIgMjY0LjM5OCAxODMuMjk0IDI2My4yNjMgMTgwLjU4NSAyNjIuNDY2QzE3OC4xNzEgMjYxLjc1NiAxNzcuMTQgMjU5LjQxIDE3Ny4zNjggMjU2LjkyM0MxNzcuNTY2IDI1NC43NjcgMTc4LjQ4NyAyNTMuOTgyIDE4MC41ODUgMjUyLjM4OFoiIGZpbGw9ImJsYWNrIi8+CjxwYXRoIGQ9Ik0yMTMuNDMgMjMxLjg5OEgyNDMuMDU5VjIzOC40NDhMMjE5LjAxOCAyNjEuMjlIMjQzLjA1OVYyNjYuNDk3SDIxMy40M1YyNTkuOTQ2TDIzNi4xMTggMjM4LjQ0OEgyMTMuNDNWMjMxLjg5OFoiIGZpbGw9ImJsYWNrIi8+CjxwYXRoIGZpbGwtcnVsZT0iZXZlbm9kZCIgY2xpcC1ydWxlPSJldmVub2RkIiBkPSJNMjgxLjE1MyAyNTIuMzg4QzI4MS4xNTMgMjUyLjM4OCAyODIuMDkgMjQ2LjY1OCAyODEuMTUzIDI0My40ODdDMjc4LjYxMiAyMzQuODc4IDI3MC45OTMgMjMxLjQyNiAyNjguMTE3IDIzMS4wNThDMjY0LjcyOSAyMzAuNjI0IDI2MS4zNDMgMjMwLjU5NCAyNTkuODIxIDIzMS4wNThDMjQ5LjQ5MSAyMzQuMjA2IDI0Ny4yOTQgMjQxLjY4MiAyNDYuOTUzIDI0Ni4wMDZDMjQ2LjYxMyAyNTAuMzMgMjQ2LjYxMyAyNTEuODQxIDI0Ni45NTMgMjUzLjU2NEMyNDguMDA3IDI1OC44OTYgMjUxLjM1MyAyNjYuMDc1IDI2Mi4xOTEgMjY4LjQ2OUMyNjYuNDY4IDI2OS40MTQgMjY5LjYwMiAyNjguODUzIDI3MC4zMTYgMjY4LjYzN0MyNzcuMDg4IDI2Ni41OTMgMjgxLjYzNCAyNjMuMTE2IDI4MiAyNTguNDM1TDI3Ny43NjcgMjU2LjkyM0MyNzYuNjMgMjU5Ljk2MyAyNzQuOTI3IDI2MS4yNSAyNzAuMTQ4IDI2Mi40NjZDMjY1LjM3IDI2My42ODEgMjYyLjE5MSAyNjIuNDY2IDI2Mi4xOTEgMjYyLjQ2NkMyNTUuNDkyIDI1OS44MDkgMjUzLjY3OCAyNTcuNTE5IDI1My4wNDggMjUyLjM4OEgyODEuMTUzWk0yNzYuNTgyIDI0Ny4xODJDMjc2LjA3NiAyNDcuNTYgMjU0LjIzNSAyNDcuMTgyIDI1My4wNDggMjQ3LjE4MkMyNTEuODYxIDI0Ny4xODIgMjU0LjM1NyAyMzcuNjYgMjYyLjE5MSAyMzYuMjY0QzI2My4zNzQgMjM2LjA1NCAyNjQuMjIxIDIzNS44ODYgMjY2LjU5MyAyMzYuMjY0QzI3My40NDQgMjM3LjM1OCAyNzcuMDg4IDI0Ni44MDMgMjc2LjU4MiAyNDcuMTgyWiIgZmlsbD0iYmxhY2siLz4KPC9zdmc+Cg==" />
      </div>

      <div class="content">
        <div class="header">
          <h1 class="plan-title">${trainingPlan.name}</h1>
          
          <div class="plan-meta">
           <div class="meta-item ${getLevelColorClass(trainingPlan.advancedLevel)}">
              <span class="meta-label">Poziom:</span> ${levelTranslations[trainingPlan.advancedLevel] || trainingPlan.advancedLevel || 'Nie wybrano'}
            </div>
            <div class="meta-item">
              <span class="meta-label">Cel:</span> ${goalTranslations[trainingPlan.goal] || trainingPlan.goal}
            </div>
            <div class="meta-item">
              <span class="meta-label">Dni:</span> ${trainingPlan.daysCount}
            </div>
            <div class="meta-item">
              <span class="meta-label">Ćwiczenia:</span> ${trainingPlan.totalExercises}
            </div>
          </div>

          ${
            trainingPlan.description
              ? `
            <div class="plan-description">${trainingPlan.description}</div>
          `
              : ''
          }
        </div>

        ${trainingPlan.days
          .map(
            (day: any, dayIndex: number) => `
          <div class="day">
            <div class="day-header ${day.isRestDay ? 'rest-day' : ''}">
              <div class="day-title">Dzień ${dayIndex + 1}: ${day.name}</div>
              ${
                !day.isRestDay && day.totalDuration > 0
                  ? `<div class="day-duration">${formatDuration(day.totalDuration)}</div>`
                  : ''
              }
            </div>

            ${
              day.isRestDay
                ? `
              <div class="exercise">
                <div class="exercise-name">Dzień odpoczynku - regeneracja</div>
              </div>
            `
                : day.exercises
                    .map(
                      (exercise: any) => `
              <div class="exercise">
                <div class="exercise-header">
                  <div class="exercise-name">${exercise.id?.name || 'Ćwiczenie'}</div>
                  ${
                    exercise.id?.muscleGroup?.name
                      ? `
                    <div class="exercise-muscle">${exercise.id.muscleGroup.name}</div>
                  `
                      : ''
                  }
                </div>

                <div class="sets-container">
                  ${exercise.sets
                    .map((set: any, setIndex: number) => {
                      let setText = `Seria ${setIndex + 1}: `;

                      if (exercise.isRepsSet) {
                        setText += `${set.repetitions} powtórzeń`;
                        if (set.weight) {
                          setText += ` × ${set.weight}kg`;
                        }
                      } else {
                        setText += `${formatSetDuration(set.duration)}`;
                        if (set.weight) {
                          setText += ` × ${set.weight}kg`;
                        }
                      }

                      return `<div class="set">${setText}</div>`;
                    })
                    .join('')}
                </div>

                ${
                  exercise.restAfterSet
                    ? `
                  <div class="rest-info">Odpoczynek: ${formatSetDuration(exercise.restAfterSet)}</div>
                `
                    : ''
                }
              </div>
            `,
                    )
                    .join('')
            }
          </div>
        `,
          )
          .join('')}

        <div class="summary">
          <div class="summary-title">Podsumowanie planu</div>
          <div class="summary-stats">
            <div class="stat">
              <div class="stat-value">${trainingPlan.days.filter((day: any) => !day.isRestDay).length}</div>
              <div class="stat-label">Dni treningowe</div>
            </div>
            <div class="stat">
              <div class="stat-value">${trainingPlan.days.filter((day: any) => day.isRestDay).length}</div>
              <div class="stat-label">Dni odpoczynku</div>
            </div>
            <div class="stat">
              <div class="stat-value">${trainingPlan.totalExercises}</div>
              <div class="stat-label">Łączna ilość ćwiczeń</div>
            </div>
            <div class="stat">
              <div class="stat-value">${formatTotalDuration(trainingPlan.totalDuration)}</div>
              <div class="stat-label">Czas trwania całego planu</div>
            </div>
          </div>
        </div>

        ${
          trainingPlan.hashtags && trainingPlan.hashtags.length > 0
            ? `
          <div class="hashtags">
            ${trainingPlan.hashtags
              .map(
                (tag: string) => `
              <div class="hashtag">#${tag}</div>
            `,
              )
              .join('')}
          </div>
        `
            : ''
        }
      </div>
    </body>
    </html>
  `;
};
