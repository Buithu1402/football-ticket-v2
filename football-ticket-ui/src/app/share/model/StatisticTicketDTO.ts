export class StatisticTicketDTO {
  constructor(
    public matchName: string = '',
    public homeLogo: string = '',
    public awayLogo: string = '',
    public totalTicket: number = 0,
    public totalTicketSold: number = 0,
    public totalTicketAvailable: number = 0,
  ) {
  }
}
