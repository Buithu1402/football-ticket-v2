export class BookingParam {
  constructor(
    public matchId: number = 0,
    public typeId: number = 0,
    public paymentMethod: string = '',
    public seatIds: number[] = []
  ) {
  }
}
